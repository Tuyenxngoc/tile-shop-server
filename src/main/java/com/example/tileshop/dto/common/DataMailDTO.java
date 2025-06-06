package com.example.tileshop.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataMailDTO {
    private String to;

    private String subject;

    private String content;

    private Map<String, Object> properties;
}
