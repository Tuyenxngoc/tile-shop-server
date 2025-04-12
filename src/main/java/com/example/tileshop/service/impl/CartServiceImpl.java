package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.cart.CartItemRequestDTO;
import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.entity.Cart;
import com.example.tileshop.entity.CartItem;
import com.example.tileshop.entity.Customer;
import com.example.tileshop.entity.Product;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ForbiddenException;
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

    /**
     * Validates the provided customer ID. If the customer ID is null,
     * a ForbiddenException is thrown to indicate that the operation
     * is not allowed.
     *
     * @param customerId the ID of the customer to validate
     * @throws ForbiddenException if the customer ID is null
     */
    private static void validateCustomerId(Long customerId) {
        if (customerId == null) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }
    }

    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setId(customerId);

                    Cart cart = new Cart();
                    cart.setCustomer(customer);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public List<CartItemResponseDTO> getCartItems(Long customerId) {
        validateCustomerId(customerId);
        Cart cart = getOrCreateCart(customerId);
        return cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(CartItemResponseDTO::new)
                .toList();
    }

    @Override
    public CommonResponseDTO addItemToCart(CartItemRequestDTO requestDTO, Long customerId) {
        validateCustomerId(customerId);
        Cart cart = getOrCreateCart(customerId);

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
            throw new BadRequestException(ErrorMessage.Product.ERR_OUT_OF_STOCK);
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.ADD);
        return new CommonResponseDTO(message, cartItem);
    }

    @Override
    public CommonResponseDTO updateCartItem(Long productId, int quantity, Long customerId) {
        validateCustomerId(customerId);
        Cart cart = getOrCreateCart(customerId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Cart.ERR_NOT_FOUND_ITEM_IN_CART, productId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, productId));

        if (quantity > product.getStockQuantity()) {
            throw new BadRequestException(ErrorMessage.Product.ERR_OUT_OF_STOCK);
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.UPDATE);
        return new CommonResponseDTO(message, cartItem);
    }

    @Override
    public CommonResponseDTO removeItemFromCart(Long productId, Long customerId) {
        validateCustomerId(customerId);
        Cart cart = getOrCreateCart(customerId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Cart.ERR_NOT_FOUND_ITEM_IN_CART, productId));

        cartItemRepository.delete(cartItem);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    @Transactional
    public CommonResponseDTO clearCart(Long customerId) {
        validateCustomerId(customerId);
        Cart cart = getOrCreateCart(customerId);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);

        String message = messageUtil.getMessage(SuccessMessage.CartItem.CLEAR);
        return new CommonResponseDTO(message);
    }

}