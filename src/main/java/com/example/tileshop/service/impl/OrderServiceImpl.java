package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.CartItemRepository;
import com.example.tileshop.repository.OrderItemRepository;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.OrderService;
import com.example.tileshop.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public Object findAll(PaginationFullRequestDTO requestDTO) {
        return null;
    }

    @Override
    public Object findById(Long id) {
        return null;
    }

    @Override
    public Object updateStatus(Long id, String status) {
        return null;
    }

    @Override
    public Object userFindAll(PaginationFullRequestDTO requestDTO) {
        return null;
    }

    @Override
    public Object userFindById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Object createOrder(OrderRequestDTO requestDTO, String userId) {
        if (requestDTO.getDeliveryMethod().equals("home_delivery")) {
            if (requestDTO.getShippingAddress() == null || requestDTO.getShippingAddress().isEmpty()) {
                throw new BadRequestException(ErrorMessage.INVALID_NOT_BLANK_FIELD);
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage(ErrorMessage.User.ERR_NOT_FOUND_ID, userId)));

        List<CartItem> cartItems = cartItemRepository.findByCartUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException(ErrorMessage.Cart.ERR_EMPTY_CART);
        }

        Order order = new Order();
        order.setNote(requestDTO.getNote());
        order.setDeliveryMethod(requestDTO.getDeliveryMethod());
        order.setShippingAddress(requestDTO.getShippingAddress());
        order.setPaymentMethod(requestDTO.getPaymentMethod());
        order.setUser(user);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new ConflictException(ErrorMessage.Product.ERR_OUT_OF_STOCK);
            }

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

        return null;
    }
}