package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.brand.BrandRequestDTO;
import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.Brand;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.BrandMapper;
import com.example.tileshop.repository.BrandRepository;
import com.example.tileshop.service.BrandService;
import com.example.tileshop.specification.BrandSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    private final MessageUtil messageUtil;

    private final UploadFileUtil uploadFileUtil;

    private Brand getEntity(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public void init() {
        if (brandRepository.count() > 0) {
            return;
        }

        Map<String, String> brandNameSlugMap = Map.of(
                "Morris", "bon-cau-morris",
                "Innoci", "bon-cau-innoci",
                "Grohe", "bon-cau-grohe",
                "American Standard", "bon-cau-american-standard",
                "Demuhler", "bon-cau-demuhler",
                "Inax", "bon-cau-inax",
                "Toto", "bon-cau-toto"
        );

        for (Map.Entry<String, String> entry : brandNameSlugMap.entrySet()) {
            String name = entry.getKey();
            String slug = entry.getValue();

            if (!brandRepository.existsByName(name)) {
                Brand brand = new Brand();
                brand.setName(name);
                brand.setSlug(slug);
                brandRepository.save(brand);
            }
        }
    }

    @Override
    public CommonResponseDTO save(BrandRequestDTO requestDTO, MultipartFile image) {
        if (brandRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.Brand.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        if (brandRepository.existsBySlug(requestDTO.getSlug())) {
            throw new ConflictException(ErrorMessage.Brand.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
        }

        Brand brand = BrandMapper.toEntity(requestDTO);

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
            }
            String imageUrl = uploadFileUtil.uploadFile(image);
            brand.setLogoUrl(imageUrl);
        }

        brandRepository.save(brand);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, BrandMapper.toDTO(brand));
    }

    @Override
    public CommonResponseDTO update(Long id, BrandRequestDTO requestDTO, MultipartFile image) {
        Brand brand = getEntity(id);

        if (!requestDTO.getName().equals(brand.getName())) {
            if (brandRepository.existsByName(requestDTO.getName())) {
                throw new ConflictException(ErrorMessage.Brand.ERR_DUPLICATE_NAME, requestDTO.getName());
            }
            brand.setName(requestDTO.getName());
        }

        if (!requestDTO.getSlug().equals(brand.getSlug())) {
            if (brandRepository.existsBySlug(requestDTO.getSlug())) {
                throw new ConflictException(ErrorMessage.Brand.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
            }
            brand.setName(requestDTO.getName());
        }

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
            }
            String oldImageUrl = brand.getLogoUrl();

            String newImageUrl = uploadFileUtil.uploadFile(image);
            brand.setLogoUrl(newImageUrl);

            if (oldImageUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldImageUrl);
            }
        }

        brand.setDescription(requestDTO.getDescription());

        brandRepository.save(brand);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, BrandMapper.toDTO(brand));
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        Brand brand = getEntity(id);

        if (!brand.getProducts().isEmpty()) {
            throw new BadRequestException(ErrorMessage.Brand.ERR_DELETE_HAS_PRODUCTS, id);
        }

        if (brand.getLogoUrl() != null) {
            uploadFileUtil.destroyFileWithUrl(brand.getLogoUrl());
        }

        brandRepository.delete(brand);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    public PaginationResponseDTO<BrandResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.BRAND);

        Page<Brand> page = brandRepository.findAll(BrandSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()), pageable);

        List<BrandResponseDTO> items = page.getContent().stream()
                .map(BrandMapper::toDTO)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.BRAND, page);

        PaginationResponseDTO<BrandResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public BrandResponseDTO findById(Long id) {
        Brand brand = getEntity(id);

        return BrandMapper.toDTO(brand);
    }

    @Override
    public BrandResponseDTO findBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID, slug));

        return BrandMapper.toDTO(brand);
    }
}