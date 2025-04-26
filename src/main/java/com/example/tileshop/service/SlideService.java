package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.slide.SlideRequestDTO;
import com.example.tileshop.entity.Slide;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SlideService {
    CommonResponseDTO save(SlideRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO update(String id, SlideRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO delete(String id);

    List<Slide> findAll();

    Slide findById(String id);
}