package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDto;
import com.example.tileshop.dto.request.CategoryRequestDto;
import com.example.tileshop.dto.request.pagination.PaginationFullRequestDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public CommonResponseDto save(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDto.getName());
        }

        Category parentCategory = null;
        if (requestDto.getParentId() != null) {
            parentCategory = getEntity(requestDto.getParentId());
        }

        List<Attribute> attributes = null;
        if (requestDto.getAttributeIds() != null) {
            attributes = attributeRepository.findAllById(requestDto.getAttributeIds());
        }

        Category category = new Category();
        category.setName(requestDto.getName());
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
        return new CommonResponseDto(message, category);
    }

    @Override
    public CommonResponseDto update(Long id, CategoryRequestDto requestDto) {
        Category category = getEntity(id);

        if (!category.getName().equals(requestDto.getName()) && categoryRepository.existsByName(requestDto.getName())) {
            throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDto.getName());
        }

        if (requestDto.getParentId() != null) {
            if (requestDto.getParentId().equals(id)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_SELF_PARENT);
            }

            Category newParent = getEntity(requestDto.getParentId());
            if (isChildCategory(id, newParent)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_CHILD_AS_PARENT);
            }

            category.setParent(newParent);
        }

        return null;
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
    public CommonResponseDto delete(Long id) {
        return null;
    }

    @Override
    public Object findAll(PaginationFullRequestDto requestDto) {
        return null;
    }

    @Override
    public Object findById(Long id) {
        return null;
    }
}