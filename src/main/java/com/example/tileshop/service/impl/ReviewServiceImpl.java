package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PaginationSortRequestDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.dto.review.CreateReviewRequestDTO;
import com.example.tileshop.dto.review.ReviewResponseDTO;
import com.example.tileshop.dto.review.ReviewSummaryResponseDTO;
import com.example.tileshop.entity.Customer;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.ReviewImage;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.CustomerRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.repository.ReviewRepository;
import com.example.tileshop.service.ReviewService;
import com.example.tileshop.specification.ReviewSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final UploadFileUtil uploadFileUtil;

    private final ProductRepository productRepository;
    
    private final CustomerRepository customerRepository;

    private final MessageUtil messageUtil;

    private Review getEntity(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Review.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public PaginationResponseDTO<ReviewResponseDTO> getReviewsByProductId(Long productId, PaginationSortRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.REVIEW);

        Specification<Review> spec = Specification
                .where(ReviewSpecification.filterByProductId(productId))
                .and(ReviewSpecification.filterByStatus(ReviewStatus.APPROVED));

        Page<Review> page = reviewRepository.findAll(spec, pageable);

        List<ReviewResponseDTO> items = page.getContent().stream()
                .map(ReviewResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.REVIEW, page);

        PaginationResponseDTO<ReviewResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public ReviewSummaryResponseDTO getReviewSummary(Long productId) {
        List<Review> approvedReviews = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        int totalReviews = approvedReviews.size();

        // Nếu chưa có review, mặc định 5 sao và map với 5 -> 100%, còn lại 0%
        if (totalReviews == 0) {
            Map<Integer, ReviewSummaryResponseDTO.RatingDetail> defaultBreakdown = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                ReviewSummaryResponseDTO.RatingDetail detail = new ReviewSummaryResponseDTO.RatingDetail();
                if (i == 5) {
                    detail.setCount(1);
                    detail.setPercentage(100.0);
                } else {
                    detail.setCount(0);
                    detail.setPercentage(0.0);
                }
                defaultBreakdown.put(i, detail);
            }

            return new ReviewSummaryResponseDTO(5.0, 1, defaultBreakdown);
        }

        double totalRating = 0.0;
        Map<Integer, Integer> ratingCountMap = new HashMap<>();

        // Khởi tạo count cho từng mức sao
        for (int i = 1; i <= 5; i++) {
            ratingCountMap.put(i, 0);
        }

        // Duyệt qua từng review để tính tổng điểm và đếm số sao
        for (Review review : approvedReviews) {
            int rating = review.getRating();
            totalRating += rating;
            ratingCountMap.put(rating, ratingCountMap.get(rating) + 1);
        }

        double averageRating = totalRating / totalReviews;
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        // Duyệt qua từng mức sao để tính phần trăm
        Map<Integer, ReviewSummaryResponseDTO.RatingDetail> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            int count = ratingCountMap.get(i);
            double percentage = (count * 100.0) / totalReviews;
            percentage = Math.round(percentage * 10.0) / 10.0;

            ReviewSummaryResponseDTO.RatingDetail detail = new ReviewSummaryResponseDTO.RatingDetail();
            detail.setCount(count);
            detail.setPercentage(percentage);

            breakdown.put(i, detail);
        }

        return new ReviewSummaryResponseDTO(averageRating, totalReviews, breakdown);
    }

    @Override
    public PaginationResponseDTO<ReviewResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.REVIEW);

        Page<Review> page = reviewRepository.findAll(ReviewSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()), pageable);

        List<ReviewResponseDTO> items = page.getContent().stream()
                .map(ReviewResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.REVIEW, page);

        PaginationResponseDTO<ReviewResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public CommonResponseDTO addReview(CreateReviewRequestDTO requestDTO, List<MultipartFile> images,  String userId) {
        //Kiểm tra hình ảnh
        if (images != null) {
            if (images.size() > 3) {
                throw new BadRequestException(ErrorMessage.Review.ERR_MAX_IMAGES);
            }
            if (images.stream().anyMatch(uploadFileUtil::isImageInvalid)) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }
        }

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, requestDTO.getProductId()));
        
        Customer customer = customerRepository.findByUserId(userId)
        		  .orElseThrow(() -> new NotFoundException(ErrorMessage.Customer.ERR_NOT_FOUND_ID, userId));
        
        long pendingReviewCount = reviewRepository.countByCustomerAndProductAndStatus(customer, product, ReviewStatus.PENDING);

    	if (pendingReviewCount >= 5) {
    	    throw new BadRequestException(ErrorMessage.Review.ERR_PENDING_LIMIT);
    	}

        Review review = new Review();

        //Tải hình ảnh lên cloud
        if (images != null) {
            List<ReviewImage> productImages = images.stream()
                    .map(image -> {
                        String newImageUrl = uploadFileUtil.uploadFile(image);
                        ReviewImage reviewImage = new ReviewImage();
                        reviewImage.setImageUrl(newImageUrl);
                        reviewImage.setReview(review);
                        return reviewImage;
                    })
                    .toList();
            review.setImages(productImages);
        }

        review.setComment(requestDTO.getComment().trim());
        review.setRating(requestDTO.getRating());
        review.setProduct(product);
        review.setCustomer(customer);

        reviewRepository.save(review);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, review);
    }

    @Override
    public CommonResponseDTO approveReview(Long id, String username) {
        Review review = getEntity(id);

        if (!ReviewStatus.APPROVED.equals(review.getStatus())) {
            review.setStatus(ReviewStatus.APPROVED);
            review.setApprovedBy(username);
            reviewRepository.save(review);
        }

        String message = messageUtil.getMessage(SuccessMessage.Review.APPROVE);
        return new CommonResponseDTO(message, review);
    }

    @Override
    public CommonResponseDTO rejectReview(Long id) {
        Review review = getEntity(id);

        if (!ReviewStatus.REJECTED.equals(review.getStatus())) {
            review.setStatus(ReviewStatus.REJECTED);
            reviewRepository.save(review);
        }

        String message = messageUtil.getMessage(SuccessMessage.Review.REJECT);
        return new CommonResponseDTO(message, review);
    }

    @Override
    public CommonResponseDTO deleteReview(Long id) {
        Review review = getEntity(id);

        // Handle old images
        Iterator<ReviewImage> iterator = review.getImages().iterator();
        while (iterator.hasNext()) {
            ReviewImage oldImage = iterator.next();
            uploadFileUtil.destroyFileWithUrl(oldImage.getImageUrl());
            iterator.remove();
        }

        reviewRepository.delete(review);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

}