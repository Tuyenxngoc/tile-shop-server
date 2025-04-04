package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.dto.product.ProductRequestDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.repository.BrandRepository;
import com.example.tileshop.repository.CategoryRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.ProductService;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.SlugUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.News.ERR_NOT_FOUND_ID, id));
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    @Override
    public CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images) {
        if (images.stream().anyMatch(uploadFileUtil::isImageInvalid)) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID,
                        requestDTO.getCategoryId()));

        Brand brand = null;
        if (requestDTO.getBrandId() != null) {
            brand = brandRepository.findById(requestDTO.getBrandId())
                    .orElseThrow(
                            () -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID, requestDTO.getBrandId()));
        }

        Product product = new Product();

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

        List<ProductImage> productImages = images.stream()
                .map(image -> {
                    String newImageUrl = uploadFileUtil.uploadFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(newImageUrl);
                    productImage.setProduct(product);
                    return productImage;
                })
                .toList();

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setDiscountPercentage(requestDTO.getDiscountPercentage());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setAverageRating(5.0);
        product.setCategory(category);
        product.setBrand(brand);
        product.setAttributes(productAttributes);
        product.setImages(productImages);

        String baseSlug = SlugUtil.toSlug(requestDTO.getName());
        String uniqueSlug = generateUniqueSlug(baseSlug);
        product.setSlug(uniqueSlug);

        productRepository.save(product);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, product);
    }

    @Override
    public CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images) {
        Product product = getEntity(id);

        if (images != null && images.stream().anyMatch(uploadFileUtil::isImageInvalid)) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID,
                        requestDTO.getCategoryId()));
        product.setCategory(category);

        Brand brand = null;
        if (requestDTO.getBrandId() != null) {
            brand = brandRepository.findById(requestDTO.getBrandId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Brand.ERR_NOT_FOUND_ID,
                            requestDTO.getBrandId()));
        }
        product.setBrand(brand);

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setDiscountPercentage(requestDTO.getDiscountPercentage());
        product.setStockQuantity(requestDTO.getStockQuantity());

        String baseSlug = SlugUtil.toSlug(requestDTO.getName());
        if (!baseSlug.equals(product.getSlug())) {
            product.setSlug(generateUniqueSlug(baseSlug));
        }

        product.getAttributes().clear();

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
        product.setAttributes(updatedAttributes);

        if (images != null && !images.isEmpty()) {
            product.getImages().clear();

            List<ProductImage> updatedImages = images.stream()
                    .map(image -> {
                        String newImageUrl = uploadFileUtil.uploadFile(image);
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(newImageUrl);
                        productImage.setProduct(product);
                        return productImage;
                    })
                    .toList();

            product.setImages(updatedImages);
        }

        productRepository.save(product);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, product);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        Product product = getEntity(id);
        //todo
        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message, null);
    }

    @Override
    public PaginationResponseDTO<ProductResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.PRODUCT);

        Page<Product> page = productRepository.findAll(pageable);

        List<ProductResponseDTO> items = page.getContent().stream()
                .map(ProductResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.PRODUCT, page);

        PaginationResponseDTO<ProductResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public ProductRequestDTO findById(Long id) {
        Product product = getEntity(id);
        ProductRequestDTO responseDTO = new ProductRequestDTO();
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setDiscountPercentage(product.getDiscountPercentage());
        responseDTO.setStockQuantity(product.getStockQuantity());
        responseDTO.setCategoryId(product.getCategory().getId());
        responseDTO.setBrandId(product.getBrand() == null ? null : product.getBrand().getId());
        responseDTO.setAttributes(product.getAttributes().stream()
                .map(attr -> {
                            ProductAttributeRequestDTO a = new ProductAttributeRequestDTO();
                            a.setAttributeId(attr.getAttribute().getId());
                            a.setValue(attr.getValue());
                            return a;
                        }
                ).toList());
        return responseDTO;
    }

    @Override
    public Object findBySlug(String slug) {
        return null;
    }
}