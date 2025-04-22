package com.example.tileshop.service.impl;

import com.example.tileshop.constant.*;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.order.OrderForUserResponseDTO;
import com.example.tileshop.dto.order.OrderPaymentResponseDTO;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.*;
import com.example.tileshop.service.OrderService;
import com.example.tileshop.specification.OrderSpecification;
import com.example.tileshop.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final MessageUtil messageUtil;

    private final ProductRepository productRepository;

    @Override
    public PaginationResponseDTO<OrderResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        return null;
    }

    @Override
    public OrderResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public CommonResponseDTO updateStatus(Long id, String status) {
        return null;
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
        order.setNote(requestDTO.getNote());
        order.setDeliveryMethod(requestDTO.getDeliveryMethod().getValue());
        order.setShippingAddress(requestDTO.getShippingAddress());
        order.setPaymentMethod(requestDTO.getPaymentMethod());
        if (!PaymentMethod.COD.equals(order.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }
        order.setUser(user);
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
            totalAmount += orderItem.getPriceAtTimeOfOrder();
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
        cartItemRepository.deleteByCartUserId(userId);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, new OrderPaymentResponseDTO(order));
    }

}