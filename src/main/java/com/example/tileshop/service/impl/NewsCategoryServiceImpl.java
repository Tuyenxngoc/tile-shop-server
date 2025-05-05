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
import com.example.tileshop.mapper.NewsCategoryMapper;
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
import java.util.Map;

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

        Map<String, String> categorySlugMap = Map.of(
                "Tư vấn thiết bị vệ sinh", "tu-van-thiet-bi-ve-sinh",
                "Tư vấn thiết bị bếp", "tu-van-thiet-bi-bep",
                "Khuyến mãi - Sự kiện", "khuyen-mai-su-kien",
                "Cẩm nang thiết kế", "cam-nang-thiet-ke",
                "Phong thủy", "phong-thuy",
                "Hỏi đáp", "hoi-dap",
                "Tuyển dụng", "tuyen-dung"
        );

        for (Map.Entry<String, String> entry : categorySlugMap.entrySet()) {
            String name = entry.getKey();
            String slug = entry.getValue();

            if (!newscategoryRepository.existsByName(name)) {
                NewsCategory newsCategory = new NewsCategory();
                newsCategory.setName(name);
                newsCategory.setSlug(slug);
                newscategoryRepository.save(newsCategory);
            }
        }
    }

    @Override
    public CommonResponseDTO save(NewsCategoryRequestDTO requestDTO) {
        if (newscategoryRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.NewsCategory.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        if (newscategoryRepository.existsBySlug(requestDTO.getSlug())) {
            throw new ConflictException(ErrorMessage.NewsCategory.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
        }

        NewsCategory newsCategory = new NewsCategory();
        newsCategory.setName(requestDTO.getName());
        newsCategory.setSlug(requestDTO.getSlug());

        newscategoryRepository.save(newsCategory);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, newsCategory);
    }

    @Override
    public CommonResponseDTO update(Long id, NewsCategoryRequestDTO requestDTO) {
        NewsCategory newsCategory = getEntity(id);

        if (!requestDTO.getName().equals(newsCategory.getName())) {
            if (newscategoryRepository.existsByName(requestDTO.getName())) {
                throw new ConflictException(ErrorMessage.NewsCategory.ERR_DUPLICATE_NAME, requestDTO.getName());
            }
            newsCategory.setName(requestDTO.getName());
        }

        if (!requestDTO.getSlug().equals(newsCategory.getSlug())) {
            if (newscategoryRepository.existsBySlug(requestDTO.getSlug())) {
                throw new ConflictException(ErrorMessage.NewsCategory.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
            }
            newsCategory.setSlug(requestDTO.getSlug());
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
                .map(NewsCategoryMapper::toDTO)
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

        return NewsCategoryMapper.toDTO(category);
    }
}