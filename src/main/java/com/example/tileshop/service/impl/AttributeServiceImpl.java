package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.attribute.AttributeRequestDTO;
import com.example.tileshop.dto.attribute.AttributeResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.Attribute;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.service.AttributeService;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;

    private final MessageUtil messageUtil;

    private Attribute getEntity(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Attribute.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDTO save(AttributeRequestDTO requestDTO) {
        if (attributeRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.Attribute.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        Attribute attribute = new Attribute();
        attribute.setName(requestDTO.getName());
        attribute.setRequired(requestDTO.getIsRequired());
        attribute.setDefaultValue(requestDTO.getDefaultValue());

        attributeRepository.save(attribute);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, attribute);
    }

    @Override
    public CommonResponseDTO update(Long id, AttributeRequestDTO requestDTO) {
        Attribute attribute = getEntity(id);

        if (!attribute.getName().equals(requestDTO.getName()) && attributeRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.Attribute.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        attribute.setName(requestDTO.getName());
        attribute.setRequired(requestDTO.getIsRequired());
        attribute.setDefaultValue(requestDTO.getDefaultValue());

        attributeRepository.save(attribute);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, attribute);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        Attribute attribute = getEntity(id);

        attributeRepository.delete(attribute);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message, null);
    }

    @Override
    public PaginationResponseDTO<AttributeResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.ATTRIBUTE);

        Page<Attribute> page = attributeRepository.findAll(pageable);

        List<AttributeResponseDTO> items = page.getContent().stream()
                .map(AttributeResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.ATTRIBUTE, page);

        PaginationResponseDTO<AttributeResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public AttributeResponseDTO findById(Long id) {
        Attribute attribute = getEntity(id);

        return new AttributeResponseDTO(attribute);
    }

    @Override
    public List<AttributeResponseDTO> findByCategoryId(Long categoryId) {
        List<Attribute> attributes = attributeRepository.findAllByCategoryAttributes_Category_Id(categoryId);
        return attributes.stream()
                .map(AttributeResponseDTO::new)
                .toList();
    }

}