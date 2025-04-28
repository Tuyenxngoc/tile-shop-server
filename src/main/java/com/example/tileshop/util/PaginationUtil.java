package com.example.tileshop.util;

import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationRequestDTO;
import com.example.tileshop.dto.pagination.PaginationSortRequestDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtil {
    public static Pageable buildPageable(PaginationRequestDTO request) {
        return PageRequest.of(request.getPageNum(), request.getPageSize());
    }

    public static Pageable buildPageable(PaginationSortRequestDTO requestDTO, SortByDataConstant constant) {
        Sort sort = Sort.by(requestDTO.getSortBy(constant));
        sort = requestDTO.getIsAscending() ? sort.ascending() : sort.descending();
        return PageRequest.of(requestDTO.getPageNum(), requestDTO.getPageSize(), sort);
    }

    public static Pageable buildPageable(PaginationRequestDTO requestDTO, List<String> sortByFields, List<Boolean> sortDirections) {
        if (sortByFields.size() != sortDirections.size()) {
            throw new IllegalArgumentException("Number of sort fields and sort directions must match");
        }

        Sort sort = Sort.by(sortByFields.stream()
                .map(field -> sortDirections.get(sortByFields.indexOf(field)) ? Sort.Order.asc(field) : Sort.Order.desc(field))
                .toArray(Sort.Order[]::new)
        );

        return PageRequest.of(requestDTO.getPageNum(), requestDTO.getPageSize(), sort);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationRequestDTO request, Page<T> pages) {
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), null, null, pages);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationSortRequestDTO request, SortByDataConstant constant, Page<T> pages) {
        String sortBy = request.getSortBy(constant);
        String sortOrder = request.getIsAscending() ? CommonConstant.SORT_TYPE_ASC : CommonConstant.SORT_TYPE_DESC;
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), sortBy, sortOrder, pages);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationFullRequestDTO request, SortByDataConstant constant, Page<T> pages) {
        String sortBy = request.getSortBy(constant);
        String sortOrder = request.getIsAscending() ? CommonConstant.SORT_TYPE_ASC : CommonConstant.SORT_TYPE_DESC;
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), sortBy, sortOrder, pages, request.getKeyword(), request.getSearchBy());
    }

    private static <T> PagingMeta buildPagingMeta(int pageNum, int pageSize, String sortBy, String sortOrder, Page<T> pages, String keyword, String searchBy) {
        return new PagingMeta(
                pages.getTotalElements(),
                pages.getTotalPages(),
                pageNum + CommonConstant.ONE_INT_VALUE,
                pageSize,
                sortBy,
                sortOrder,
                keyword,
                searchBy
        );
    }

    private static <T> PagingMeta buildPagingMeta(int pageNum, int pageSize, String sortBy, String sortOrder, Page<T> pages) {
        return buildPagingMeta(pageNum, pageSize, sortBy, sortOrder, pages, null, null);
    }
}