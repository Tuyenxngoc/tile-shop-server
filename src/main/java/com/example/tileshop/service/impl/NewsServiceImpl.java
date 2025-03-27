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
import com.example.tileshop.entity.NewsCategory;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.NewsMapper;
import com.example.tileshop.repository.NewsCategoryRepository;
import com.example.tileshop.repository.NewsRepository;
import com.example.tileshop.service.NewsService;
import com.example.tileshop.specification.NewsSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.SlugUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    private final MessageUtil messageUtil;

    private final NewsMapper newsMapper;

    private final UploadFileUtil uploadFileUtil;

    private final NewsCategoryRepository newsCategoryRepository;

    private News getEntity(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.News.ERR_NOT_FOUND_ID, id));
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (newsRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    private void deleteImagesInContent(String content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        // Regex tìm tất cả URL trong bất kỳ đoạn chứa `src="..."` hoặc `src='...'`
        Pattern pattern = Pattern.compile("src=['\"]([^'\"]+)['\"]");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String imageUrl = matcher.group(1); // Lấy URL ảnh
            uploadFileUtil.destroyFileWithUrl(imageUrl); // Xóa ảnh
        }
    }

    @Override
    public CommonResponseDTO save(NewsRequestDTO requestDTO, MultipartFile image) {
        if (uploadFileUtil.isImageInvalid(image)) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
        }

        NewsCategory newsCategory = newsCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NewsCategory.ERR_NOT_FOUND_ID, requestDTO.getCategoryId()));

        String newImageUrl = uploadFileUtil.uploadFile(image);

        String baseSlug = SlugUtil.toSlug(requestDTO.getTitle());
        String uniqueSlug = generateUniqueSlug(baseSlug);

        News news = newsMapper.toNews(requestDTO);
        news.setSlug(uniqueSlug);
        news.setCategory(newsCategory);
        news.setImageUrl(newImageUrl);

        newsRepository.save(news);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, news);
    }

    @Override
    public CommonResponseDTO update(Long id, NewsRequestDTO requestDTO, MultipartFile image) {
        News news = getEntity(id);

        if (!news.getTitle().equals(requestDTO.getTitle())) {
            String baseSlug = SlugUtil.toSlug(requestDTO.getTitle());
            String uniqueSlug = generateUniqueSlug(baseSlug);
            news.setSlug(uniqueSlug);
        }

        if (!news.getCategory().getId().equals(requestDTO.getCategoryId())) {
            NewsCategory newsCategory = newsCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.NewsCategory.ERR_NOT_FOUND_ID, requestDTO.getCategoryId()));
            news.setCategory(newsCategory);
        }

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }
            String oldImageUrl = news.getImageUrl();

            String newImageUrl = uploadFileUtil.uploadFile(image);
            news.setImageUrl(newImageUrl);

            if (oldImageUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldImageUrl);
            }
        }

        news.setTitle(requestDTO.getTitle());
        news.setDescription(requestDTO.getDescription());
        news.setContent(requestDTO.getContent());

        newsRepository.save(news);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, news);
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        News news = getEntity(id);

        if (news.getImageUrl() != null) {
            uploadFileUtil.destroyFileWithUrl(news.getImageUrl());
        }

        deleteImagesInContent(news.getContent());

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

    @Override
    public NewsResponseDTO findBySlug(String slug) {
        News news = newsRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.News.ERR_NOT_FOUND_SLUG, slug));

        return new NewsResponseDTO(news);
    }

}