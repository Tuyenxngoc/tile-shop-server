package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.category.CategoryRequestDTO;
import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.CategoryAttribute;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.repository.CategoryAttributeRepository;
import com.example.tileshop.repository.CategoryRepository;
import com.example.tileshop.service.CategoryService;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final AttributeRepository attributeRepository;

    private final CategoryAttributeRepository categoryAttributeRepository;

    private final MessageUtil messageUtil;

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDTO save(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        Category parentCategory = null;
        if (requestDTO.getParentId() != null) {
            parentCategory = getEntity(requestDTO.getParentId());
        }

        List<Attribute> attributes = null;
        if (requestDTO.getAttributeIds() != null) {
            attributes = attributeRepository.findAllById(requestDTO.getAttributeIds());
        }

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setParent(parentCategory);

        categoryRepository.save(category);

        if (attributes != null) {
            List<CategoryAttribute> categoryAttributes = new ArrayList<>();
            for (Attribute attribute : attributes) {
                CategoryAttribute categoryAttribute = new CategoryAttribute();
                categoryAttribute.setCategory(category);
                categoryAttribute.setAttribute(attribute);

                categoryAttributes.add(categoryAttribute);
            }

            categoryAttributeRepository.saveAll(categoryAttributes);
        }

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, category);
    }

    @Override
    public CommonResponseDTO update(Long id, CategoryRequestDTO requestDTO) {
        Category category = getEntity(id);

        if (!category.getName().equals(requestDTO.getName())) {
            if (categoryRepository.existsByName(requestDTO.getName())) {
                throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDTO.getName());
            }
            category.setName(requestDTO.getName());
        }

        if (requestDTO.getParentId() != null) {
            if (requestDTO.getParentId().equals(id)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_SELF_PARENT);
            }

            Category newParent = getEntity(requestDTO.getParentId());
            if (isChildCategory(id, newParent)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_CHILD_AS_PARENT);
            }

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        // Lấy danh sách thuộc tính hiện tại
        Set<Long> currentAttributeIds = category.getCategoryAttributes()
                .stream()
                .map(ca -> ca.getAttribute().getId())
                .collect(Collectors.toSet());

        // Lấy danh sách thuộc tính mới từ request
        Set<Long> newAttributeIds = requestDTO.getAttributeIds() != null
                ? requestDTO.getAttributeIds()
                : new HashSet<>();

        // Xác định danh sách cần xóa (có trong current nhưng không có trong new)
        Set<Long> attributesToRemove = new HashSet<>(currentAttributeIds);
        attributesToRemove.removeAll(newAttributeIds);

        // Xác định danh sách cần thêm (có trong new nhưng không có trong current)
        Set<Long> attributesToAdd = new HashSet<>(newAttributeIds);
        attributesToAdd.removeAll(currentAttributeIds);

        // Xóa những CategoryAttribute không còn cần thiết
        if (!attributesToRemove.isEmpty()) {
            categoryAttributeRepository.deleteByCategoryIdAndAttributeIdIn(id, attributesToRemove);
        }

        // Thêm những CategoryAttribute mới
        if (!attributesToAdd.isEmpty()) {
            List<Attribute> attributes = attributeRepository.findAllById(attributesToAdd);
            List<CategoryAttribute> categoryAttributes = attributes.stream().map(attribute -> {
                CategoryAttribute categoryAttribute = new CategoryAttribute();
                categoryAttribute.setCategory(category);
                categoryAttribute.setAttribute(attribute);
                return categoryAttribute;
            }).collect(Collectors.toList());

            categoryAttributeRepository.saveAll(categoryAttributes);
        }

        // Lưu danh mục vào database
        categoryRepository.save(category);

        // Trả về phản hồi thành công
        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, category);
    }

    private boolean isChildCategory(Long id, Category parentCategory) {
        while (parentCategory != null) {
            if (parentCategory.getId().equals(id)) {
                return true;
            }
            parentCategory = parentCategory.getParent();
        }
        return false;
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        Category category = getEntity(id);

        if (!category.getSubCategories().isEmpty()) {
            throw new BadRequestException(ErrorMessage.Category.ERR_HAS_CHILDREN);
        }

        categoryRepository.delete(category);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message, null);
    }

    @Override
    public PaginationResponseDTO<CategoryResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.CATEGORY);

        Page<Category> page = categoryRepository.findAll(pageable);

        List<CategoryResponseDTO> items = page.getContent().stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.CATEGORY, page);

        PaginationResponseDTO<CategoryResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category category = getEntity(id);

        return new CategoryResponseDTO(category);
    }
}