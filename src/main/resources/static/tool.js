const fs = require('fs');
const path = require('path');

const BASE_DIR = path.join(__dirname, '..', '..', '..', '..');
const JAVA_CODE_DIR = path.join(BASE_DIR, 'src', 'main', 'java', 'com', 'example', 'tileshop');

const ENTITY_DIR = path.join(JAVA_CODE_DIR, 'entity');
const OUTPUT_DIRS = {
    controllers: path.join(JAVA_CODE_DIR, 'controller'),
    repositories: path.join(JAVA_CODE_DIR, 'repository'),
    services: path.join(JAVA_CODE_DIR, 'service'),
    serviceImpl: path.join(JAVA_CODE_DIR, 'service', 'impl'),
    mappers: path.join(JAVA_CODE_DIR, 'mapper'),
    dtos: path.join(JAVA_CODE_DIR, 'dto')
};

// Đảm bảo các thư mục đầu ra tồn tại
Object.values(OUTPUT_DIRS).forEach(dir => {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, {recursive: true});
    }
});

// Hàm lấy danh sách entity từ thư mục
function getEntities() {
    if (!fs.existsSync(ENTITY_DIR)) return [];
    return fs.readdirSync(ENTITY_DIR)
        .filter(file => file.endsWith('.java'))
        .map(file => path.basename(file, '.java')); // Lấy tên file không có đuôi
}

// Hàm tạo file với nội dung
function createFile(filePath, content) {
    if (!fs.existsSync(filePath)) {
        fs.writeFileSync(filePath, content);
        console.log(`✅ Created: ${filePath}`);
    } else {
        console.log(`⚠️ File exists, skipped: ${filePath}`);
    }
}

// Nội dung Controller
function getControllerContent(entity) {
    return `package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.service.${entity}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "${entity}")
public class ${entity}Controller {
    ${entity}Service ${entity.toLowerCase()}Service;
}`;
}

// Nội dung Repository
function getRepositoryContent(entity) {
    return `package com.example.tileshop.repository;

import com.example.tileshop.entity.${entity};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entity}Repository extends JpaRepository<${entity}, Long> {
}`;
}

// Nội dung Service
function getServiceContent(entity) {
    return `package com.example.tileshop.service;

import com.example.tileshop.entity.${entity};

import java.util.List;

public interface ${entity}Service {
    List<${entity}> getAll();
}`;
}

// Nội dung ServiceImpl
function getServiceImplContent(entity) {
    return `package com.example.tileshop.service.impl;

import com.example.tileshop.entity.${entity};
import com.example.tileshop.repository.${entity}Repository;
import com.example.tileshop.service.${entity}Service;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ${entity}ServiceImpl implements ${entity}Service {
    private final ${entity}Repository ${entity.toLowerCase()}Repository;

    @Override
    public List<${entity}> getAll() {
        return ${entity.toLowerCase()}Repository.findAll();
    }
}`;
}

// Nội dung DTO
function getDTOContent(entity) {
    return `package com.example.tileshop.dto;

import lombok.Data;

@Data
public class ${entity}DTO {
    private Long id;
}`;
}

// Nội dung Mapper
function getMapperContent(entity) {
    return `package com.example.tileshop.mapper;

import com.example.tileshop.entity.${entity};
import com.example.tileshop.dto.${entity.toLowerCase()}.${entity}ResponseDTO;

public class ${entity}Mapper {

    public static ${entity}ResponseDTO toDTO(${entity} ${entity.toLowerCase()}) {
        if (${entity.toLowerCase()} == null) {
            return null;
        }

        ${entity}ResponseDTO dto = new ${entity}ResponseDTO();
        // TODO: set fields từ ${entity} vào dto
        // vd: dto.setId(${entity.toLowerCase()}.getId());

        return dto;
    }

}`;
}

// **Chạy tool**
function generate() {
    const entities = getEntities();
    if (entities.length === 0) {
        console.log('⚠️ No entities found in', ENTITY_DIR);
        return;
    }

    console.log(`🔍 Found entities: ${entities.join(', ')}`);

    entities.forEach(entity => {
        createFile(path.join(OUTPUT_DIRS.controllers, `${entity}Controller.java`), getControllerContent(entity));
        createFile(path.join(OUTPUT_DIRS.repositories, `${entity}Repository.java`), getRepositoryContent(entity));
        createFile(path.join(OUTPUT_DIRS.services, `${entity}Service.java`), getServiceContent(entity));
        createFile(path.join(OUTPUT_DIRS.serviceImpl, `${entity}ServiceImpl.java`), getServiceImplContent(entity));
        createFile(path.join(OUTPUT_DIRS.mappers, `${entity}Mapper.java`), getMapperContent(entity));
    });

    console.log('✅ Generation complete!');
}

generate();
