package com.example.tileshop.service.impl;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.entity.StoreInfo;
import com.example.tileshop.service.StoreInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j2
@Service
@RequiredArgsConstructor
public class StoreInfoServiceImpl implements StoreInfoService {

    private static final String filePath = "data/store-info.json";

    private final ObjectMapper objectMapper;

    @Override
    public StoreInfo getStoreInfo() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return objectMapper.readValue(file, StoreInfo.class);
            }
        } catch (IOException e) {
            log.error("Error while reading store info from file: {}", filePath, e);
        }
        return null;
    }

    @Override
    public CommonResponseDTO updateStore(StoreInfoRequestDTO requestDTO) {
        try {
            StoreInfo storeInfo = getStoreInfo();
            if (storeInfo != null) {
                storeInfo.setName(requestDTO.getName());
                storeInfo.setAddress(requestDTO.getAddress());

                objectMapper.writeValue(new File(filePath), storeInfo);

                return new CommonResponseDTO("Cập nhật thông tin cửa hàng thành công", true);
            } else {
                return new CommonResponseDTO("Không tìm thấy thông tin cửa hàng", false);
            }
        } catch (IOException e) {
            log.error("Error while updating store info", e);
            return new CommonResponseDTO("Lỗi khi cập nhật thông tin cửa hàng", false);
        }
    }

}
