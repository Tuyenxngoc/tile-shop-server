package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.filter.ProductFilterDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PaginationSortRequestDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.dto.product.ProductDetailResponseDTO;
import com.example.tileshop.dto.product.ProductRequestDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.product.ProductUpdateResponseDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.ProductMapper;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.repository.BrandRepository;
import com.example.tileshop.repository.CategoryRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.ProductService;
import com.example.tileshop.specification.ProductSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final AttributeRepository attributeRepository;

    private final MessageUtil messageUtil;

    private final UploadFileUtil uploadFileUtil;

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images) {
        if (images.stream().anyMatch(uploadFileUtil::isImageInvalid)) {
            throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
        }

        if (productRepository.existsBySlug(requestDTO.getSlug())) {
            throw new BadRequestException(ErrorMessage.Product.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
        }

        // Khởi tạo product
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setSlug(requestDTO.getSlug());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setDiscountPercentage(requestDTO.getDiscountPercentage());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setAverageRating(5.0);

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID,
                        requestDTO.getCategoryId()));
        product.setCategory(category);

        if (requestDTO.getBrandId() != null) {
            Brand brand = brandRepository.findById(requestDTO.getBrandId())
                    .orElseThrow(
                            () -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID, requestDTO.getBrandId()));
            product.setBrand(brand);
        }

        List<ProductAttribute> productAttributes = Optional.ofNullable(requestDTO.getAttributes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(attrDTO -> attrDTO.getValue() != null && !attrDTO.getValue().trim().isEmpty())
                .map(attrDTO -> {
                    Attribute attribute = attributeRepository.findById(attrDTO.getAttributeId())
                            .orElseThrow(() -> new NotFoundException(ErrorMessage.Attribute.ERR_NOT_FOUND_ID,
                                    attrDTO.getAttributeId()));

                    ProductAttribute productAttribute = new ProductAttribute();
                    productAttribute.setValue(attrDTO.getValue());
                    productAttribute.setAttribute(attribute);
                    productAttribute.setProduct(product);
                    return productAttribute;
                })
                .toList();
        product.setAttributes(productAttributes);

        List<ProductImage> productImages = images.stream()
                .map(image -> {
                    String newImageUrl = uploadFileUtil.uploadFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(newImageUrl);
                    productImage.setProduct(product);
                    return productImage;
                })
                .toList();
        product.setImages(productImages);

        productRepository.save(product);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, product);
    }

    @Override
    @Transactional
    public CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images, Set<String> existingImageUrls) {
        // Validate images
        if (images != null && images.stream().anyMatch(uploadFileUtil::isImageInvalid)) {
            throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
        }

        Product product = getEntity(id);

        // Validate slug
        if (!requestDTO.getSlug().equals(product.getSlug()) && productRepository.existsBySlug(requestDTO.getSlug())) {
            throw new BadRequestException(ErrorMessage.Product.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
        }

        product.setName(requestDTO.getName());
        product.setSlug(requestDTO.getSlug());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setDiscountPercentage(requestDTO.getDiscountPercentage());
        product.setStockQuantity(requestDTO.getStockQuantity());

        // Update Category
        if (!requestDTO.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID,
                            requestDTO.getCategoryId()));
            product.setCategory(category);
        }

        // Update Brand
        if (requestDTO.getBrandId() != null) {
            Long newBrandId = requestDTO.getBrandId();
            Long currentBrandId = (product.getBrand() != null) ? product.getBrand().getId() : null;

            if (!newBrandId.equals(currentBrandId)) {
                Brand brand = brandRepository.findById(newBrandId)
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID, newBrandId));
                product.setBrand(brand);
            }
        }

        // Update attributes
        List<ProductAttribute> updatedAttributes = Optional.ofNullable(requestDTO.getAttributes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(attrDTO -> attrDTO.getValue() != null && !attrDTO.getValue().trim().isEmpty())
                .map(attrDTO -> {
                    Attribute attribute = attributeRepository.findById(attrDTO.getAttributeId())
                            .orElseThrow(() -> new NotFoundException(ErrorMessage.Attribute.ERR_NOT_FOUND_ID,
                                    attrDTO.getAttributeId()));
                    ProductAttribute productAttribute = new ProductAttribute();
                    productAttribute.setAttribute(attribute);
                    productAttribute.setValue(attrDTO.getValue());
                    productAttribute.setProduct(product);
                    return productAttribute;
                })
                .toList();
        product.getAttributes().clear();
        product.getAttributes().addAll(updatedAttributes);

        // Upload new images
        List<ProductImage> updatedImages = Optional.ofNullable(images)
                .orElse(Collections.emptyList())
                .stream()
                .map(image -> {
                    String newImageUrl = uploadFileUtil.uploadFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(newImageUrl);
                    productImage.setProduct(product);
                    return productImage;
                })
                .toList();

        // Handle old images
        Iterator<ProductImage> productImageIterator = product.getImages().iterator();
        while (productImageIterator.hasNext()) {
            ProductImage oldImage = productImageIterator.next();
            if (existingImageUrls == null || !existingImageUrls.contains(oldImage.getImageUrl())) {
                uploadFileUtil.destroyFileWithUrl(oldImage.getImageUrl());
                productImageIterator.remove();
            }
        }

        // Add new images
        product.getImages().addAll(updatedImages);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, product);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        Product product = getEntity(id);
        if (!product.getOrderItems().isEmpty()) {
            throw new BadRequestException(ErrorMessage.Product.ERR_EXIST_ORDER_ITEMS);
        }
        if (!product.getCartItems().isEmpty()) {
            throw new BadRequestException(ErrorMessage.Product.ERR_EXIST_CART_ITEMS);
        }

        productRepository.delete(product);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    public PaginationResponseDTO<ProductResponseDTO> findAll(PaginationFullRequestDTO requestDTO, ProductFilterDTO filterDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.PRODUCT);

        Specification<Product> spec = Specification.where(ProductSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()))
                .and(ProductSpecification.filterByProductFilterDTO(filterDTO));

        Page<Product> page = productRepository.findAll(spec, pageable);

        List<ProductResponseDTO> items = page.getContent().stream()
                .map(ProductMapper::toResponseDTO)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.PRODUCT, page);

        PaginationResponseDTO<ProductResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public ProductUpdateResponseDTO findById(Long id) {
        Product product = getEntity(id);

        return ProductMapper.toUpdateResponseDTO(product);
    }

    @Override
    public ProductDetailResponseDTO findBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_SLUG, slug));

        return ProductMapper.toDetailResponseDTO(product);
    }

    @Override
    public PaginationResponseDTO<ProductResponseDTO> searchProducts(String keyword, PaginationSortRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.PRODUCT);

        Specification<Product> spec = Specification.where(ProductSpecification.searchAllFields(keyword));

        Page<Product> page = productRepository.findAll(spec, pageable);

        List<ProductResponseDTO> items = page.getContent().stream()
                .map(ProductMapper::toResponseDTO)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.PRODUCT, page);

        PaginationResponseDTO<ProductResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }
}