package com.example.tileshop.util;

import com.ibm.icu.text.Normalizer2;

public class SlugUtil {

    public static String toSlug(String input) {
        input = input.replace("Đ", "D")
                .replace("đ", "d");

        Normalizer2 normalizer = Normalizer2.getNFDInstance();
        String normalized = normalizer.normalize(input);

        return normalized.replaceAll("\\p{M}", "")  // Xóa dấu
                .replaceAll("[^a-zA-Z0-9\\s-]", "")  // Xóa ký tự đặc biệt
                .replaceAll("\\s+", "-")  // Thay thế khoảng trắng bằng dấu gạch ngang
                .toLowerCase();
    }

}
