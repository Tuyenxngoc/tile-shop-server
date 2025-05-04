package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.SortByDataConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.category.CategoryRequestDTO;
import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.dto.category.CategoryTreeResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
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
import com.example.tileshop.specification.CategorySpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final AttributeRepository attributeRepository;

    private final CategoryAttributeRepository categoryAttributeRepository;

    private final MessageUtil messageUtil;

    private final UploadFileUtil uploadFileUtil;

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public void init() {
        if (categoryRepository.count() > 0) {
            return;
        }

        Map<String, String> parentCategoryMap = Map.of(
                "Thiết Bị Vệ Sinh", "thiet-bi-ve-sinh",
                "Gạch Ốp Lát", "gach-op-lat"
        );

        Map<String, Map<String, String>> childCategoryMap = Map.of(
                "Thiết Bị Vệ Sinh", Map.of(
                        "Bồn Tắm", "bon-tam",
                        "Phòng Xông Hơi", "phong-xong-hoi",
                        "Bồn Cầu", "bon-cau",
                        "Bồn Tiểu Nam", "bon-tieu-nam",
                        "Lavabo", "lavabo",
                        "Sen Tắm", "sen-tam",
                        "Vòi Lavabo", "voi-lavabo",
                        "Combo Thiết Bị Vệ Sinh", "combo-thiet-bi-ve-sinh",
                        "Phụ Kiện Phòng Tắm", "phu-kien-phong-tam",
                        "Điều Hoà Phòng Tắm", "dieu-hoa-phong-tam"
                ),
                "Gạch Ốp Lát", Map.of(
                        "Gạch Lát Nền", "gach-lat-nen",
                        "Gạch Ốp Tường", "gach-op-tuong"
                )
        );

        Map<String, Map<String, String>> subChildCategoryMap = Map.of(
                "Phụ Kiện Phòng Tắm", Map.of(
                        "Giá Khăn", "gia-khan",
                        "Lô giấy", "lo-giay",
                        "Thoát Sàn", "thoat-san",
                        "Vòi xịt vệ sinh", "voi-xit-ve-sinh",
                        "Giá treo đựng đồ", "gia-treo-dung-do",
                        "Máy sấy tay", "may-say-tay",
                        "Bộ phụ kiện", "bo-phu-kien"
                )
        );

        for (String parentName : parentCategoryMap.keySet()) {
            Category parentCategory = new Category();
            parentCategory.setName(parentName);
            parentCategory.setSlug(parentCategoryMap.get(parentName));
            parentCategory.setParent(null);
            categoryRepository.save(parentCategory);

            Map<String, String> subCategories = childCategoryMap.get(parentName);
            if (subCategories != null) {
                for (String childName : subCategories.keySet()) {
                    Category childCategory = new Category();
                    childCategory.setName(childName);
                    childCategory.setSlug(subCategories.get(childName));
                    childCategory.setParent(parentCategory);
                    categoryRepository.save(childCategory);

                    Map<String, String> subSubCategories = subChildCategoryMap.get(childName);
                    if (subSubCategories != null) {
                        for (String subChildName : subSubCategories.keySet()) {
                            Category subChildCategory = new Category();
                            subChildCategory.setName(subChildName);
                            subChildCategory.setSlug(subSubCategories.get(subChildName));
                            subChildCategory.setParent(childCategory);
                            categoryRepository.save(subChildCategory);
                        }
                    }
                }
            }
        }
    }

    @Override
    public CommonResponseDTO save(CategoryRequestDTO requestDTO, MultipartFile image) {
        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDTO.getName());
        }

        if (categoryRepository.existsBySlug(requestDTO.getSlug())) {
            throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
        }

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setSlug(requestDTO.getSlug());

        if (requestDTO.getParentId() != null) {
            Category parentCategory = getEntity(requestDTO.getParentId());

            category.setParent(parentCategory);
        }

        if (requestDTO.getAttributeIds() != null) {
            List<Attribute> attributes = attributeRepository.findAllById(requestDTO.getAttributeIds());
            List<CategoryAttribute> categoryAttributes = attributes.stream()
                    .map(attribute -> {
                        CategoryAttribute categoryAttribute = new CategoryAttribute();
                        categoryAttribute.setCategory(category);
                        categoryAttribute.setAttribute(attribute);
                        return categoryAttribute;
                    })
                    .toList();

            category.setCategoryAttributes(categoryAttributes);
        }

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }
            String imageUrl = uploadFileUtil.uploadFile(image);
            category.setImageUrl(imageUrl);
        }

        categoryRepository.save(category);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, category);
    }

    @Override
    @Transactional
    public CommonResponseDTO update(Long id, CategoryRequestDTO requestDTO, MultipartFile image) {
        Category category = getEntity(id);

        if (!requestDTO.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(requestDTO.getName())) {
                throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_NAME, requestDTO.getName());
            }
            category.setName(requestDTO.getName());
        }

        if (!requestDTO.getSlug().equals(category.getSlug())) {
            if (categoryRepository.existsBySlug(requestDTO.getSlug())) {
                throw new ConflictException(ErrorMessage.Category.ERR_DUPLICATE_SLUG, requestDTO.getSlug());
            }
            category.setSlug(requestDTO.getSlug());
        }

        if (requestDTO.getParentId() != null) {
            if (requestDTO.getParentId().equals(id)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_SELF_PARENT);
            }

            Category newParent = getEntity(requestDTO.getParentId());
            if (isChildCategory(id, newParent)) {
                throw new BadRequestException(ErrorMessage.Category.ERR_CHILD_AS_PARENT);
            }

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        if (image != null && !image.isEmpty()) {
            if (uploadFileUtil.isImageInvalid(image)) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }
            String oldImageUrl = category.getImageUrl();

            String newImageUrl = uploadFileUtil.uploadFile(image);
            category.setImageUrl(newImageUrl);

            if (oldImageUrl != null) {
                uploadFileUtil.destroyFileWithUrl(oldImageUrl);
            }
        }

        // Lấy danh sách thuộc tính hiện tại
        Set<Long> currentAttributeIds = category.getCategoryAttributes()
                .stream()
                .map(ca -> ca.getAttribute().getId())
                .collect(Collectors.toSet());

        // Lấy danh sách thuộc tính mới từ request
        Set<Long> newAttributeIds = requestDTO.getAttributeIds() != null
                ? requestDTO.getAttributeIds()
                : new HashSet<>();

        // Xác định danh sách cần xóa (có trong current nhưng không có trong new)
        Set<Long> attributesToRemove = new HashSet<>(currentAttributeIds);
        attributesToRemove.removeAll(newAttributeIds);

        // Xác định danh sách cần thêm (có trong new nhưng không có trong current)
        Set<Long> attributesToAdd = new HashSet<>(newAttributeIds);
        attributesToAdd.removeAll(currentAttributeIds);

        // Xóa những CategoryAttribute không còn cần thiết
        if (!attributesToRemove.isEmpty()) {
            category.getCategoryAttributes().removeIf(ca -> attributesToRemove.contains(ca.getAttribute().getId()));
        }

        // Thêm những CategoryAttribute mới
        if (!attributesToAdd.isEmpty()) {
            List<Attribute> attributes = attributeRepository.findAllById(attributesToAdd);
            List<CategoryAttribute> categoryAttributes = attributes.stream()
                    .map(attribute -> {
                        CategoryAttribute categoryAttribute = new CategoryAttribute();
                        categoryAttribute.setCategory(category);
                        categoryAttribute.setAttribute(attribute);
                        return categoryAttribute;
                    }).toList();

            categoryAttributeRepository.saveAll(categoryAttributes);
        }

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, category);
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
    public CommonResponseDTO delete(Long id) {
        Category category = getEntity(id);

        if (!category.getSubCategories().isEmpty()) {
            throw new BadRequestException(ErrorMessage.Category.ERR_HAS_CHILDREN);
        }

        categoryRepository.delete(category);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    public PaginationResponseDTO<CategoryResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.CATEGORY);

        Page<Category> page = categoryRepository.findAll(CategorySpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword()), pageable);

        List<CategoryResponseDTO> items = page.getContent().stream()
                .map(CategoryResponseDTO::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.CATEGORY, page);

        PaginationResponseDTO<CategoryResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category category = getEntity(id);

        return new CategoryResponseDTO(category);
    }

    @Override
    public List<CategoryTreeResponseDTO> getCategoriesTree() {
        List<Category> categories = categoryRepository.findAll();

        return buildCategoryTree(categories, null);
    }

    private List<CategoryTreeResponseDTO> buildCategoryTree(List<Category> categories, Category parent) {
        List<CategoryTreeResponseDTO> tree = new ArrayList<>();
        for (Category category : categories) {
            if ((parent == null && category.getParent() == null) || (parent != null && category.getParent() != null && category.getParent().getId().equals(parent.getId()))) {
                CategoryTreeResponseDTO categoryTreeDTO = new CategoryTreeResponseDTO(
                        category.getId(),
                        category.getName(),
                        buildCategoryTree(categories, category)
                );
                tree.add(categoryTreeDTO);
            }
        }
        return tree;
    }
}