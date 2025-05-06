package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.storeinfo.StoreInfoRequestDTO;
import com.example.tileshop.entity.StoreInfo;
import com.example.tileshop.exception.BadGatewayException;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.service.StoreInfoService;
import com.example.tileshop.util.UploadFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StoreInfoServiceImpl implements StoreInfoService {
    private static final String filePath = "data/store-info.json";

    private final UploadFileUtil uploadFileUtil;

    private final ObjectMapper objectMapper;

    private StoreInfo storeInfoCache = null;

    private StoreInfo readStoreInfoFromFile() {
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

    private void writeStoreInfoToFile(StoreInfo storeInfo) throws IOException {
        try {
            objectMapper.writeValue(new File(filePath), storeInfo);
        } catch (IOException e) {
            log.error("Failed to write store info to file", e);
            throw e;
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (uploadFileUtil.isImageInvalid(file)) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
        }
    }

    @PostConstruct
    private void init() {
        this.storeInfoCache = readStoreInfoFromFile();
    }

    @Override
    public StoreInfo getStoreInfo() {
        return storeInfoCache;
    }

    @Override
    public synchronized CommonResponseDTO updateStore(StoreInfoRequestDTO requestDTO, MultipartFile logo, MultipartFile logoSmall, MultipartFile bannerImage, MultipartFile backgroundImage) {
        if (storeInfoCache == null) {
            return new CommonResponseDTO("Không tìm thấy thông tin cửa hàng");
        }

        // Validate file type
        if (logo != null && !logo.isEmpty()) {
            validateImageFile(logo);
        }
        if (logoSmall != null && !logoSmall.isEmpty()) {
            validateImageFile(logoSmall);
        }
        if (bannerImage != null && !bannerImage.isEmpty()) {
            validateImageFile(bannerImage);
        }
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            validateImageFile(backgroundImage);
        }

        List<String> uploadedFileUrls = new ArrayList<>();
        String oldLogoUrl = storeInfoCache.getLogo();
        String oldLogoSmallUrl = storeInfoCache.getLogoSmall();
        String oldBannerUrl = storeInfoCache.getBannerImage();
        String oldBackgroundUrl = storeInfoCache.getBackgroundImage();

        try {
            // Upload Logo
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = uploadFileUtil.uploadFile(logo);
                uploadedFileUrls.add(logoUrl);
                storeInfoCache.setLogo(logoUrl);
            }

            // Upload Logo small
            if (logoSmall != null && !logoSmall.isEmpty()) {
                String logoSmallUrl = uploadFileUtil.uploadFile(logoSmall);
                uploadedFileUrls.add(logoSmallUrl);
                storeInfoCache.setLogoSmall(logoSmallUrl);
            }

            // Upload Banner Image
            if (bannerImage != null && !bannerImage.isEmpty()) {
                String bannerUrl = uploadFileUtil.uploadFile(bannerImage);
                uploadedFileUrls.add(bannerUrl);
                storeInfoCache.setBannerImage(bannerUrl);
            }

            // Upload Background Image
            if (backgroundImage != null && !backgroundImage.isEmpty()) {
                String backgroundUrl = uploadFileUtil.uploadFile(backgroundImage);
                uploadedFileUrls.add(backgroundUrl);
                storeInfoCache.setBackgroundImage(backgroundUrl);
            }

            storeInfoCache.setName(requestDTO.getName());
            storeInfoCache.setAddress(requestDTO.getAddress());
            storeInfoCache.setPhone(requestDTO.getPhone());
            storeInfoCache.setPhoneSupport(requestDTO.getPhoneSupport());
            storeInfoCache.setEmail(requestDTO.getEmail());
            storeInfoCache.setOpeningHours(requestDTO.getOpeningHours());
            storeInfoCache.setFacebookUrl(requestDTO.getFacebookUrl());
            storeInfoCache.setYoutubeUrl(requestDTO.getYoutubeUrl());
            storeInfoCache.setZaloUrl(requestDTO.getZaloUrl());
            storeInfoCache.setBannerLink(requestDTO.getBannerLink());
            storeInfoCache.setBackgroundColor(requestDTO.getBackgroundColor());

            writeStoreInfoToFile(storeInfoCache);

            if (logo != null && !logo.isEmpty() && oldLogoUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldLogoUrl);
            }
            if (logoSmall != null && !logoSmall.isEmpty() && oldLogoSmallUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldLogoSmallUrl);
            }
            if (bannerImage != null && !bannerImage.isEmpty() && oldBannerUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldBannerUrl);
            }
            if (backgroundImage != null && !backgroundImage.isEmpty() && oldBackgroundUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldBackgroundUrl);
            }

            return new CommonResponseDTO("Cập nhật thông tin cửa hàng thành công", storeInfoCache);
        } catch (BadGatewayException e) {
            // Rollback: Xoá các file đã upload thành công
            for (String url : uploadedFileUrls) {
                try {
                    uploadFileUtil.destroyFileWithUrl(url);
                } catch (Exception ex) {
                    log.warn("Không thể xoá file khi rollback: {}", url, ex);
                }
            }
            return new CommonResponseDTO("Lỗi khi cập nhật thông tin cửa hàng");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
