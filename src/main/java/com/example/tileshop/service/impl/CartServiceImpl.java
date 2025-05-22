package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.cart.CartItemRequestDTO;
import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.entity.Cart;
import com.example.tileshop.entity.CartItem;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.CartItemRepository;
import com.example.tileshop.repository.CartRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.CartService;
import com.example.tileshop.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    @Value("${cart.max-items}")
    private int maxItems;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final MessageUtil messageUtil;

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setId(userId);

                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public List<CartItemResponseDTO> getCartItems(String userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(CartItemResponseDTO::new)
                .toList();
    }

    @Override
    public CommonResponseDTO addItemToCart(CartItemRequestDTO requestDTO, String userId) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, requestDTO.getProductId()));

        // Tìm cartItem hiện tại nếu có
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), requestDTO.getProductId());

        // Nếu là item mới → kiểm tra giới hạn số lượng sản phẩm trong giỏ
        if (optionalCartItem.isEmpty()) {
            long totalItems = cartItemRepository.countByCartId(cart.getId());
            if (totalItems >= maxItems) {
                throw new BadRequestException(ErrorMessage.Cart.ERR_MAX_ITEMS, maxItems);
            }
        }

        CartItem cartItem = optionalCartItem.orElseGet(() -> {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            return newItem;
        });

        int newQuantity = cartItem.getQuantity() + requestDTO.getQuantity();
        if (newQuantity > product.getStockQuantity()) {
            throw new BadRequestException(ErrorMessage.Product.ERR_OUT_OF_STOCK, product.getName(), product.getStockQuantity());
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.ADD);
        return new CommonResponseDTO(message, new CartItemResponseDTO(cartItem));
    }

    @Override
    public CommonResponseDTO updateCartItem(Long productId, int quantity, String userId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CartItem.ERR_NOT_FOUND_ID, productId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, productId));

        if (quantity > product.getStockQuantity()) {
            throw new BadRequestException(ErrorMessage.Product.ERR_OUT_OF_STOCK, product.getName(), product.getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.UPDATE);
        return new CommonResponseDTO(message, new CartItemResponseDTO(cartItem));
    }

    @Override
    public CommonResponseDTO removeItemFromCart(Long productId, String userId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CartItem.ERR_NOT_FOUND_ID, productId));

        cartItemRepository.delete(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    @Transactional
    public CommonResponseDTO clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);

        String message = messageUtil.getMessage(SuccessMessage.Cart.CLEAR);
        return new CommonResponseDTO(message);
    }
}