package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.OrderService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Order")
public class OrderController {

    OrderService orderService;

    // -------------------- ADMIN APIs --------------------

    @Operation(summary = "API Get All Orders")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Order.Admin.GET_ALL)
    public ResponseEntity<?> getAllOrders(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(orderService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Order By Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Order.Admin.GET_BY_ID)
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return VsResponseUtil.success(orderService.findById(id));
    }

    @Operation(summary = "API Update Order Status")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Order.Admin.UPDATE_STATUS)
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam(name = "status") OrderStatus status) {
        return VsResponseUtil.success(orderService.updateStatus(id, status));
    }

    // -------------------- USER APIs --------------------

    @Operation(summary = "User - API Get All Orders")
    @GetMapping(UrlConstant.Order.User.GET_ALL)
    public ResponseEntity<?> getUserOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String keyword,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(orderService.userFindAll(userDetails.getUserId(), status, keyword));
    }

    @Operation(summary = "User - API Get Order By Id")
    @GetMapping(UrlConstant.Order.User.GET_BY_ID)
    public ResponseEntity<?> getUserOrderById(@PathVariable Long id, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(orderService.userFindById(id, userDetails.getUserId()));
    }

    @Operation(summary = "User - API Create Order")
    @PostMapping(UrlConstant.Order.User.CREATE)
    public ResponseEntity<?> userCreateOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(HttpStatus.CREATED, orderService.createOrder(orderRequestDTO, userDetails.getUserId()));
    }

}