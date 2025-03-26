package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.dto.news.NewsResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.entity.News;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.NewsMapper;
import com.example.tileshop.repository.NewsRepository;
import com.example.tileshop.service.NewsService;
import com.example.tileshop.specification.NewsSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    private final MessageUtil messageUtil;

    private final NewsMapper newsMapper;

    private News getEntity(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.News.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDTO save(NewsRequestDTO requestDTO, MultipartFile image) {

        News news = newsMapper.toNews(requestDTO);

        newsRepository.save(news);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, news);
    }

    @Override
    public CommonResponseDTO update(Long id, NewsRequestDTO requestDTO, MultipartFile image) {
        News news = getEntity(id);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, news);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        News news = getEntity(id);

        newsRepository.delete(news);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message, id);
    }

    @Override
    public PaginationResponseDTO<NewsResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.NEWS);

        Page<News> page = newsRepository.findAll(NewsSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()), pageable);

        List<NewsResponseDTO> items = page.getContent().stream()
                .map(NewsResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.NEWS, page);

        PaginationResponseDTO<NewsResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public NewsResponseDTO findById(Long id) {
        News news = getEntity(id);

        return new NewsResponseDTO(news);
    }

}