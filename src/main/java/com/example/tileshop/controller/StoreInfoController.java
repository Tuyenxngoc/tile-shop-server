package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.service.StoreInfoService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Store")
public class StoreInfoController {

    StoreInfoService storeInfoService;

    @Operation(summary = "API Get Store Info")
    @GetMapping(UrlConstant.StoreInfo.GET)
    public ResponseEntity<?> getStoreInfo() {
        return VsResponseUtil.success(storeInfoService.getStoreInfo());
    }

    @Operation(summary = "API Update Store Info")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.StoreInfo.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateStore(
            @RequestPart("store") @Valid StoreInfoRequestDTO requestDTO,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "logoSmall", required = false) MultipartFile logoSmall,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
            @RequestPart(value = "backgroundImage", required = false) MultipartFile backgroundImage
    ) {
        return VsResponseUtil.success(storeInfoService.updateStore(requestDTO, logo, logoSmall, bannerImage, backgroundImage));
    }

}
