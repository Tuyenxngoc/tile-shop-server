package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.slide.SlideRequestDTO;
import com.example.tileshop.entity.Slide;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.service.SlideService;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.UploadFileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlideServiceImpl implements SlideService {
    private static final TypeReference<List<Slide>> SLIDES_LIST_RESPONSE_TYPE =
            new TypeReference<>() {
            };

    private static final String filePath = "data/slide.json";

    private final UploadFileUtil uploadFileUtil;

    private final ObjectMapper objectMapper;

    private final MessageUtil messageUtil;

    private List<Slide> slideCache;

    @PostConstruct
    private void init() {
        this.slideCache = readSlidesFromFile();
    }

    private List<Slide> readSlidesFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, SLIDES_LIST_RESPONSE_TYPE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read slides from file", e);
        }
    }

    private void writeSlidesToFile(List<Slide> slides) {
        try {
            objectMapper.writeValue(new File(filePath), slides);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write slides to file", e);
        }
    }

    @Override
    public synchronized CommonResponseDTO save(SlideRequestDTO requestDTO, MultipartFile image) {
        if (uploadFileUtil.isImageInvalid(image)) {
            throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
        }

        String imageUrl = uploadFileUtil.uploadFile(image);

        Slide slide = new Slide();
        slide.setId(UUID.randomUUID().toString());
        slide.setIndex(requestDTO.getIndex());
        slide.setLink(requestDTO.getLink());
        slide.setDescription(requestDTO.getDescription());
        slide.setImageUrl(imageUrl);

        slideCache.add(slide);
        writeSlidesToFile(slideCache);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, slide);
    }

    @Override
    public synchronized CommonResponseDTO update(String id, SlideRequestDTO requestDTO, MultipartFile image) {
        Slide slide = slideCache.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Slide.ERR_NOT_FOUND_ID, id));

        slide.setIndex(requestDTO.getIndex());
        slide.setLink(requestDTO.getLink());
        slide.setDescription(requestDTO.getDescription());

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_IMAGE_FILE_TYPE);
            }
            String oldImageUrl = slide.getImageUrl();
            String newImageUrl = uploadFileUtil.uploadFile(image);
            slide.setImageUrl(newImageUrl);

            if (oldImageUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldImageUrl);
            }
        }

        writeSlidesToFile(slideCache);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, slide);
    }

    @Override
    public synchronized CommonResponseDTO delete(String id) {
        Slide slide = slideCache.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Slide.ERR_NOT_FOUND_ID, id));

        if (slide.getImageUrl() != null) {
            uploadFileUtil.destroyFileWithUrl(slide.getImageUrl());
        }

        slideCache.remove(slide);
        writeSlidesToFile(slideCache);

        return new CommonResponseDTO(messageUtil.getMessage(SuccessMessage.DELETE), slide);
    }

    @Override
    public List<Slide> findAll() {
        List<Slide> sortedSlides = new ArrayList<>(slideCache);

        Collections.sort(sortedSlides);

        return sortedSlides;
    }

    @Override
    public Slide findById(String id) {
        return slideCache.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Slide.ERR_NOT_FOUND_ID, id));
    }
}