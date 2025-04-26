package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.service.StoreInfoService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Store")
public class StoreInfoController {

    StoreInfoService storeInfoService;

    @Operation(summary = "API Get Store Info")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.StoreInfo.GET)
    public ResponseEntity<?> getStoreInfo() {
        return VsResponseUtil.success(storeInfoService.getStoreInfo());
    }

    @Operation(summary = "API Update Store Info")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.StoreInfo.UPDATE)
    public ResponseEntity<?> updateStore(@RequestBody StoreInfoRequestDTO storeInfoRequestDTO) {
        return VsResponseUtil.success(storeInfoService.updateStore(storeInfoRequestDTO));
    }

}
