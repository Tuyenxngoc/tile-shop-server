package com.example.tileshop.service.impl;

import com.example.tileshop.constant.*;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.filter.OrderFilterRequestDTO;
import com.example.tileshop.dto.order.OrderForUserResponseDTO;
import com.example.tileshop.dto.order.OrderPaymentResponseDTO;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.OrderMapper;
import com.example.tileshop.repository.*;
import com.example.tileshop.service.OrderService;
import com.example.tileshop.specification.OrderSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final MessageUtil messageUtil;

    private final ProductRepository productRepository;

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
            throw new BadRequestException(ErrorMessage.Order.ERR_SAME_STATUS, newStatus.name());
        }

        // Kiểm tra tính hợp lệ của việc chuyển trạng thái
        if (!canTransition(currentStatus, newStatus)) {
            throw new BadRequestException(ErrorMessage.Order.ERR_INVALID_STATUS_TRANSITION, currentStatus.name(), newStatus.name());
        }

        order.setStatus(newStatus);

        // Nếu trạng thái mới là DELIVERED thì set PaymentStatus thành PAID (nếu phương thức thanh toán không phải COD)
        if (OrderStatus.DELIVERED.equals(newStatus) && !PaymentMethod.COD.equals(order.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        orderRepository.save(order);

        String message = messageUtil.getMessage(SuccessMessage.Order.UPDATE_STATUS, newStatus.name());
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
    public List<OrderForUserResponseDTO> userFindAll(String userId, OrderStatus status, String keyword) {
        Specification<Order> spec = Specification.where(OrderSpecification.hasUserId(userId));

        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrderSpecification.containsKeyword(keyword));
        }

        List<Order> orders = orderRepository.findAll(spec);

        return orders.stream()
                .map(OrderForUserResponseDTO::new)
                .toList();
    }

    @Override
    public OrderResponseDTO userFindById(Long id, String userId) {
        return null;
    }

    @Override
    @Transactional
    public CommonResponseDTO createOrder(OrderRequestDTO requestDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage(ErrorMessage.User.ERR_NOT_FOUND_ID, userId)));

        List<CartItem> cartItems = cartItemRepository.findByCartUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException(ErrorMessage.Cart.ERR_EMPTY_CART);
        }

        Order order = new Order();

        order.setDeliveryMethod(requestDTO.getDeliveryMethod());
        if (!DeliveryMethod.STORE_PICKUP.equals(order.getDeliveryMethod())) {
            order.setShippingAddress(requestDTO.getShippingAddress());
        }

        order.setPaymentMethod(requestDTO.getPaymentMethod());
        if (!PaymentMethod.COD.equals(order.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }

        order.setUser(user);
        order.setNote(requestDTO.getNote());
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Kiểm tra số lượng tồn kho
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new ConflictException(ErrorMessage.Product.ERR_OUT_OF_STOCK);
            }

            // Giảm số lượng sản phẩm
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

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, new OrderPaymentResponseDTO(order));
    }

}