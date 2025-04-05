package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.CategoryAttribute;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.repository.CategoryAttributeRepository;
import com.example.tileshop.repository.CategoryRepository;
import com.example.tileshop.service.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryAttributeServiceImpl implements CategoryAttributeService {
    private final CategoryAttributeRepository categoryattributeRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeRepository attributeRepository;

    @Override
    public void init() {
        if (categoryattributeRepository.count() > 0) {
            return;
        }

        // Danh sách tên danh mục
        List<String> categoryNames = List.of(
                "Bồn Tắm",
                "Phòng Xông Hơi",
                "Bồn Cầu",
                "Bồn Tiểu Nam",
                "Lavabo",
                "Sen Tắm",
                "Vòi Lavabo",
                "Combo Thiết Bị Vệ Sinh",
                "Phụ Kiện Phòng Tắm",
                "Điều Hoà Phòng Tắm",
                "Gạch Lát Nền",
                "Gạch Ốp Tường"
        );

        // Danh sách các thuộc tính tương ứng cho mỗi danh mục
        List<List<String>> categoryAttributes = List.of(
                // Thuộc tính cho "Bồn Tắm"
                List.of("Chất liệu", "Kích thước", "Công nghệ men", "Cơ chế xả"),
                // Thuộc tính cho "Phòng Xông Hơi"
                List.of("Chất liệu", "Kích thước", "Công nghệ men", "Cơ chế xả"),
                // Thuộc tính cho "Bồn Cầu"
                List.of("Loại nắp", "Lượng nước xả", "Tâm xả", "Chất liệu"),
                // Thuộc tính cho "Bồn Tiểu Nam"
                List.of("Loại nắp", "Lượng nước xả", "Tâm xả", "Chất liệu"),
                // Thuộc tính cho "Lavabo"
                List.of("Chất liệu", "Kích thước", "Loại sản phẩm"),
                // Thuộc tính cho "Sen Tắm"
                List.of("Chất liệu", "Công nghệ men", "Kích thước"),
                // Thuộc tính cho "Vòi Lavabo"
                List.of("Chất liệu", "Công nghệ men", "Kích thước"),
                // Thuộc tính cho "Combo Thiết Bị Vệ Sinh"
                List.of("Loại sản phẩm", "Sản xuất tại", "Chất liệu"),
                // Thuộc tính cho "Phụ Kiện Phòng Tắm"
                List.of("Loại sản phẩm", "Chất liệu"),
                // Thuộc tính cho "Điều Hoà Phòng Tắm"
                List.of("Công suất", "Kích thước", "Chất liệu"),
                // Thuộc tính cho "Gạch Lát Nền"
                List.of("Kích thước", "Màu sắc", "Chất liệu"),
                // Thuộc tính cho "Gạch Ốp Tường"
                List.of("Kích thước", "Màu sắc", "Chất liệu")
        );

        Map<String, Category> categoryMap = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            categoryMap.put(category.getName(), category);
        }

        Map<String, Attribute> attributeMap = new HashMap<>();
        List<Attribute> attributes = attributeRepository.findAll();
        for (Attribute attribute : attributes) {
            attributeMap.put(attribute.getName(), attribute);
        }

        for (int i = 0; i < categoryNames.size(); i++) {
            String categoryName = categoryNames.get(i);

            Category category = categoryMap.get(categoryName);
            if (category == null) {
                continue;
            }

            List<String> attributesForCategory = categoryAttributes.get(i);
            for (String attributeName : attributesForCategory) {
                Attribute attribute = attributeMap.get(attributeName);
                if (attribute == null) {
                    continue;
                }

                CategoryAttribute categoryAttribute = new CategoryAttribute();
                categoryAttribute.setAttribute(attribute);
                categoryAttribute.setCategory(category);
                categoryattributeRepository.save(categoryAttribute);
            }
        }
    }

}