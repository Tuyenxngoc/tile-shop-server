package com.example.tileshop.service.impl;

import com.example.tileshop.constant.*;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.common.DataMailDTO;
import com.example.tileshop.dto.filter.OrderFilterRequestDTO;
import com.example.tileshop.dto.order.*;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.OrderMapper;
import com.example.tileshop.repository.CartItemRepository;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.InvoiceService;
import com.example.tileshop.service.OrderService;
import com.example.tileshop.specification.OrderSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.SendMailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Value("${order.max-pending-orders}")
    private int maxPendingOrders;

    private final OrderRepository orderRepository;

    private final CartItemRepository cartItemRepository;

    private final UserRepository userRepository;

    private final MessageUtil messageUtil;

    private final ProductRepository productRepository;

    private final InvoiceService invoiceService;

    private final SendMailUtil sendMailUtil;

    private static final Map<OrderStatus, Set<OrderStatus>> allowedTransitions = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.DELIVERING, OrderStatus.CANCELLED),
            OrderStatus.DELIVERING, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.RETURNED),
            OrderStatus.DELIVERED, EnumSet.of(OrderStatus.RETURNED),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.RETURNED, EnumSet.noneOf(OrderStatus.class)
    );

    public static boolean canTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return allowedTransitions.getOrDefault(currentStatus, EnumSet.noneOf(OrderStatus.class))
                .contains(newStatus);
    }

    private Order getEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public PaginationResponseDTO<OrderResponseDTO> findAll(OrderFilterRequestDTO filter, PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.ORDER);

        Specification<Order> spec = OrderSpecification.filterByConditions(filter).and(OrderSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()));

        Page<Order> page = orderRepository.findAll(spec, pageable);

        List<OrderResponseDTO> items = page.getContent().stream()
                .map(OrderMapper::toDTO)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.ORDER, page);

        PaginationResponseDTO<OrderResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public OrderResponseDTO findById(Long id) {
        Order order = getEntity(id);

        return OrderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public CommonResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        Order order = getEntity(id);
        OrderStatus currentStatus = order.getStatus();

        // Kiểm tra nếu trạng thái hiện tại bằng trạng thái mới
        if (currentStatus == newStatus) {
            throw new BadRequestException(ErrorMessage.Order.ERR_SAME_STATUS, newStatus.getDescription());
        }

        if (OrderStatus.CANCELLED.equals(newStatus)) {
            throw new BadRequestException("Vui lòng sử dụng API huỷ đơn hàng.");
        }

        // Kiểm tra tính hợp lệ của việc chuyển trạng thái
        if (!canTransition(currentStatus, newStatus)) {
            throw new BadRequestException(ErrorMessage.Order.ERR_INVALID_STATUS_TRANSITION, currentStatus.getDescription(), newStatus.getDescription());
        }

        // Xử lý hoàn sản phẩm khi trả hàng
        if (OrderStatus.RETURNED.equals(newStatus)) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = productRepository.findProductForUpdate(orderItem.getProduct().getId());
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);

        orderRepository.save(order);

        String message = messageUtil.getMessage(SuccessMessage.Order.UPDATE_STATUS, newStatus.getDescription());
        return new CommonResponseDTO(message, OrderMapper.toDTO(order));
    }

    @Override
    public Map<String, Long> countOrdersByStatus() {
        List<Object[]> resultList = orderRepository.countOrdersGroupByStatus();
        long totalOrders = 0;
        Map<String, Long> resultMap = new HashMap<>();
        for (Object[] row : resultList) {
            OrderStatus status = (OrderStatus) row[0];
            Long count = (Long) row[1];
            resultMap.put(status.name(), count);

            totalOrders += count;
        }
        resultMap.put("ALL", totalOrders);

        return resultMap;
    }

    @Override
    public List<OrderResponseDTO> userFindAll(String userId, OrderStatus status, String keyword) {
        Specification<Order> spec = Specification.where(OrderSpecification.hasUserId(userId));

        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrderSpecification.containsKeyword(keyword));
        }

        List<Order> orders = orderRepository.findAll(spec);

        return orders.stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO userFindById(Long id, CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(RoleConstant.ROLE_ADMIN.name()));
        if (isAdmin) {
            return OrderMapper.toDTO(getEntity(id));
        } else {
            Order order = orderRepository.findByIdAndUserId(id, userDetails.getUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, id));
            return OrderMapper.toDTO(order);
        }
    }

    @Override
    @Transactional
    public CommonResponseDTO create(OrderRequestDTO requestDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage(ErrorMessage.User.ERR_NOT_FOUND_ID, userId)));

        // Kiểm tra số lượng đơn hàng đang chờ xác nhận
        long pendingOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.PENDING);
        if (pendingOrders >= maxPendingOrders) {
            throw new BadRequestException(ErrorMessage.Order.ERR_TOO_MANY_PENDING_ORDERS, maxPendingOrders);
        }

        List<CartItem> cartItems = cartItemRepository.findByCartUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException(ErrorMessage.Cart.ERR_EMPTY_CART);
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setRecipientGender(requestDTO.getGender());
        order.setRecipientName(requestDTO.getFullName());
        order.setRecipientPhone(requestDTO.getPhoneNumber());
        order.setRecipientEmail(requestDTO.getEmail());
        order.setNote(requestDTO.getNote());

        // Nếu nhận hàng tại nhà thì cập nhật địa chỉ giao hàng
        order.setDeliveryMethod(requestDTO.getDeliveryMethod());
        if (!DeliveryMethod.STORE_PICKUP.equals(order.getDeliveryMethod())) {
            order.setShippingAddress(requestDTO.getShippingAddress());
        }

        // Nếu không phải thanh toán khi nhận hàng thì cập nhật trang thái thanh toán là đang chờ
        order.setPaymentMethod(requestDTO.getPaymentMethod());
        if (!PaymentMethod.COD.equals(order.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findProductForUpdate(cartItem.getProduct().getId());

            // Kiểm tra số lượng tồn kho
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new ConflictException(ErrorMessage.Product.ERR_OUT_OF_STOCK, product.getName(), product.getStockQuantity());
            }

            // Giảm số lượng tồn kho sản phẩm
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTimeOfOrder(product.calculateFinalPrice());

            orderItems.add(orderItem);
            totalAmount += orderItem.getPriceAtTimeOfOrder() * orderItem.getQuantity();
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
        cartItemRepository.deleteByCartUserId(userId);

        // Gửi hóa đơn qua email
        if (requestDTO.getRequestInvoice()) {
            sendOrderInvoiceByEmail(order, requestDTO.getInvoiceCompanyName(), requestDTO.getInvoiceCompanyAddress(), requestDTO.getInvoiceTaxCode());
        }

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, new OrderPaymentResponseDTO(order));
    }

    private void sendOrderInvoiceByEmail(Order order, String invoiceCompanyName, String invoiceCompanyAddress, String invoiceTaxCode) {
        final byte[] invoiceBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            invoiceService.generateCompanyInvoice(order, baos, invoiceCompanyName, invoiceCompanyAddress, invoiceTaxCode);
            invoiceBytes = baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating invoice PDF for order ID: {}", order.getId(), e);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                DataMailDTO mailDTO = new DataMailDTO();
                mailDTO.setTo(order.getRecipientEmail());
                mailDTO.setSubject("Hóa đơn mua hàng #" + order.getId());

                Map<String, Object> props = new HashMap<>();
                props.put("currentTime", new Date());
                props.put("fullName", order.getRecipientName());
                props.put("orderId", order.getId());
                props.put("recipientName", order.getRecipientName());
                props.put("shippingAddress", order.getShippingAddress());
                props.put("paymentMethod", order.getPaymentMethod().name());

                List<Map<String, Object>> orderItems = new ArrayList<>();
                for (OrderItem item : order.getOrderItems()) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productName", item.getProduct().getName());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("priceAtTimeOfOrder", item.getPriceAtTimeOfOrder());
                    orderItems.add(itemMap);
                }

                props.put("orderItems", orderItems);
                props.put("totalAmount", order.getTotalAmount());

                mailDTO.setProperties(props);

                sendMailUtil.sendMailWithAttachmentAndHtml(
                        mailDTO,
                        "invoice.html",
                        invoiceBytes,
                        "HoaDon_" + order.getId() + ".pdf"
                );
            } catch (Exception e) {
                log.error("Lỗi gửi mail hóa đơn cho order ID: {}", order.getId(), e);
            }
        });
    }

    @Override
    @Transactional
    public CommonResponseDTO cancelOrder(Long id, CancelOrderRequestDTO requestDTO, CustomUserDetails userDetails) {
        Order order = getEntity(id);

        // Kiểm tra quyền truy cập của người dùng: chỉ cho phép người đặt đơn hoặc admin hủy
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(RoleConstant.ROLE_ADMIN.name()));

        boolean isOrderOwner = order.getUser() != null && order.getUser().getId().equals(userDetails.getUserId());

        if (!isOrderOwner && !isAdmin) {
            throw new BadRequestException(ErrorMessage.Order.ERR_NOT_FOUND_ID, id);
        }

        // Kiểm tra trạng thái đơn hàng có cho phép hủy không
        if (!canTransition(order.getStatus(), OrderStatus.CANCELLED)) {
            throw new BadRequestException(ErrorMessage.Order.ERR_INVALID_STATUS_TRANSITION, order.getStatus().getDescription(), OrderStatus.CANCELLED.getDescription());
        }

        // Hoàn lại số lượng tồn kho cho từng sản phẩm trong đơn
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productRepository.findProductForUpdate(orderItem.getProduct().getId());
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        // Cập nhật trạng thái đơn hàng và lý do hủy
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(requestDTO.getCancelReason());

        orderRepository.save(order);

        String message = messageUtil.getMessage(SuccessMessage.Order.ORDER_CANCELLED);
        return new CommonResponseDTO(message, OrderMapper.toDTO(order));
    }

    @Override
    public byte[] generateOrderReport(OrderFilterRequestDTO filter) {
        List<Order> orders = orderRepository.findAll(OrderSpecification.filterByConditions(filter));

        // Tạo Workbook và Sheet
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("orders");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.LEFT);

            // Tạo tiêu đề cột
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Mã đơn hàng",
                    "Tên người nhận",
                    "Số điện thoại",
                    "Email",
                    "Địa chỉ giao hàng",
                    "Tổng tiền",
                    "Phương thức thanh toán",
                    "Tình trạng đơn hàng",
                    "Trạng thái thanh toán",
                    "Lý do hủy",
                    "Ngày tạo đơn"
            };

            // Ghi tiêu đề vào các cột
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Điền dữ liệu vào các dòng của bảng
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                Object[] values = {
                        order.getId(),
                        order.getRecipientName(),
                        order.getRecipientPhone(),
                        order.getRecipientEmail() != null ? order.getRecipientEmail() : "",
                        order.getShippingAddress() != null ? order.getShippingAddress() : "",
                        order.getTotalAmount(),
                        order.getPaymentMethod().name(),
                        order.getStatus().name(),
                        order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "",
                        order.getCancelReason() != null ? order.getCancelReason() : "",
                        order.getCreatedDate().format(dateTimeFormatter)
                };

                for (int i = 0; i < values.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(values[i].toString());
                    cell.setCellStyle(dataCellStyle);
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error while generating Excel report", e);
            throw new RuntimeException("Error while generating Excel report", e);
        }
    }

    @Override
    public byte[] generateInvoicePdf(Long id) {
        Order order = getEntity(id);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            invoiceService.generateInvoice(order, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating invoice PDF for order ID: {}", id, e);
            throw new RuntimeException("Error generating invoice PDF file", e);
        }
    }

    @Override
    public CommonResponseDTO updateOrder(Long id, OrderUpdateRequestDTO requestDTO) {
        Order order = getEntity(id);

        // Nếu đơn hàng đã hủy hoặc đã hoàn thành, không được phép cập nhật thông tin
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException(ErrorMessage.Order.ERR_CANNOT_UPDATE_FINALIZED_ORDER);
        }

        // Cập nhật thông tin người nhận
        order.setRecipientName(requestDTO.getRecipientName());
        order.setRecipientPhone(requestDTO.getRecipientPhone());
        order.setRecipientEmail(requestDTO.getRecipientEmail());
        order.setRecipientGender(requestDTO.getRecipientGender());

        // Cập nhật ghi chú
        order.setNote(requestDTO.getNote());

        // Cập nhật phương thức giao hàng và địa chỉ
        order.setDeliveryMethod(requestDTO.getDeliveryMethod());
        order.setShippingAddress(requestDTO.getShippingAddress());

        // Cập nhật lý do hủy đơn
        order.setCancelReason(requestDTO.getCancelReason());

        orderRepository.save(order);

        String message = messageUtil.getMessage(SuccessMessage.Order.UPDATE);
        return new CommonResponseDTO(message, OrderMapper.toDTO(order));
    }
}