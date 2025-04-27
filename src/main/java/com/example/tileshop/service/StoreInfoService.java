package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.entity.StoreInfo;
import org.springframework.web.multipart.MultipartFile;

public interface StoreInfoService {
    StoreInfo getStoreInfo();

    CommonResponseDTO updateStore(StoreInfoRequestDTO requestDTO, MultipartFile logo, MultipartFile logoSmall, MultipartFile bannerImage, MultipartFile backgroundImage);
}
