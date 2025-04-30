package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.newscategory.NewsCategoryRequestDTO;
import com.example.tileshop.dto.newscategory.NewsCategoryResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.NewsCategory;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.NewsCategoryRepository;
import com.example.tileshop.service.NewsCategoryService;
import com.example.tileshop.specification.NewsCategorySpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsCategoryServiceImpl implements NewsCategoryService {
    private final NewsCategoryRepository newscategoryRepository;

    private final MessageUtil messageUtil;

    private NewsCategory getEntity(Long id) {
        return newscategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NewsCategory.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public void init() {
        if (newscategoryRepository.count() > 0) {
            return;
        }

        List<String> categoryNames = List.of(
                "Tư vấn thiết bị vệ sinh",
                "Tư vấn thiết bị bếp",
                "Khuyến mãi - Sự kiện",
                "Cẩm nang thiết kế",
                "Phong thủy",
                "Hỏi đáp",
                "Tuyển dụng"
        );

        for (String name : categoryNames) {
            if (!newscategoryRepository.existsByName(name)) {
                NewsCategory newsCategory = new NewsCategory();
                newsCategory.setName(name);
                newscategoryRepository.save(newsCategory);
            }
        }
    }

    @Override
    public CommonResponseDTO save(NewsCategoryRequestDTO requestDTO) {
        if (newscategoryRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.NewsCategory.ERR_NOT_FOUND_ID, requestDTO.getName());
        }

        NewsCategory newsCategory = new NewsCategory();
        newsCategory.setName(requestDTO.getName());

        newscategoryRepository.save(newsCategory);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, newsCategory);
    }

    @Override
    public CommonResponseDTO update(Long id, NewsCategoryRequestDTO requestDTO) {
        NewsCategory newsCategory = getEntity(id);

        if (!newsCategory.getName().equals(requestDTO.getName())) {
            if (newscategoryRepository.existsByName(requestDTO.getName())) {
                throw new ConflictException(ErrorMessage.NewsCategory.ERR_DUPLICATE_NAME, requestDTO.getName());
            }
            newsCategory.setName(requestDTO.getName());
        }

        newscategoryRepository.save(newsCategory);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, newsCategory);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        NewsCategory newsCategory = getEntity(id);

        if (!newsCategory.getNews().isEmpty()) {
            throw new BadRequestException(ErrorMessage.NewsCategory.ERR_HAS_NEWS);
        }

        newscategoryRepository.delete(newsCategory);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    public PaginationResponseDTO<NewsCategoryResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.NEWS_CATEGORY);

        Page<NewsCategory> page = newscategoryRepository.findAll(NewsCategorySpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()), pageable);

        List<NewsCategoryResponseDTO> items = page.getContent().stream()
                .map(NewsCategoryResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.NEWS_CATEGORY, page);

        PaginationResponseDTO<NewsCategoryResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public NewsCategoryResponseDTO findById(Long id) {
        NewsCategory category = getEntity(id);

        return new NewsCategoryResponseDTO(category);
    }
}