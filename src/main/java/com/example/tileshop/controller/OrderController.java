package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.filter.OrderFilterRequestDTO;
import com.example.tileshop.dto.order.CancelOrderRequestDTO;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public ResponseEntity<?> getAllOrders(@ParameterObject OrderFilterRequestDTO filter, @ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(orderService.findAll(filter, requestDTO));
    }

    @Operation(summary = "API Update Order Status")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Order.Admin.UPDATE_STATUS)
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam(name = "status") OrderStatus status) {
        return VsResponseUtil.success(orderService.updateStatus(id, status));
    }

    @Operation(summary = "API Count orders by status")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Order.Admin.COUNT_BY_STATUS)
    public ResponseEntity<?> countOrdersByStatus() {
        return VsResponseUtil.success(orderService.countOrdersByStatus());
    }

    @Operation(summary = "API Generate and Download Order Report")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Order.Admin.EXPORT_REPORT)
    public ResponseEntity<byte[]> generateOrderReport(@ParameterObject OrderFilterRequestDTO filter) {
        byte[] report = orderService.generateOrderReport(filter);

        LocalDateTime fromDate = filter.getFromDate();
        LocalDateTime toDate = filter.getToDate();

        String statusString = filter.getStatus() != null ? filter.getStatus().toString() : "all";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fromDateString = (fromDate != null) ? fromDate.format(formatter) : "unknownFromDate";
        String toDateString = (toDate != null) ? toDate.format(formatter) : "unknownToDate";

        String fileName = String.format("Order.%s.%s_%s.xlsx", statusString, fromDateString, toDateString);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(report);
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

    @Operation(summary = "API Get Order By Id")
    @GetMapping(UrlConstant.Order.User.GET_BY_ID)
    public ResponseEntity<?> getUserOrderById(@PathVariable Long id, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(orderService.userFindById(id, userDetails));
    }

    @Operation(summary = "API Create Order")
    @PostMapping(UrlConstant.Order.User.CREATE)
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO requestDTO, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(HttpStatus.CREATED, orderService.create(requestDTO, userDetails.getUserId()));
    }

    @Operation(summary = "API Cancel Order")
    @PutMapping(UrlConstant.Order.User.CANCEL)
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @Valid @RequestBody CancelOrderRequestDTO requestDTO, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(orderService.cancelOrder(id, requestDTO, userDetails));
    }
}