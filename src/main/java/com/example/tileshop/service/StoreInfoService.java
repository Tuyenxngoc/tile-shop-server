package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.entity.StoreInfo;

public interface StoreInfoService {
    StoreInfo getStoreInfo();

    CommonResponseDTO updateStore(StoreInfoRequestDTO requestDTO);
}
