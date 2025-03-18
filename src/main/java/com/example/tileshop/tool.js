const fs = require('fs');
const path = require('path');

// Cấu hình thư mục chứa entity và thư mục đầu ra
const ENTITY_DIR = path.join(__dirname, 'domain/entity');
const OUTPUT_DIRS = {
    controllers: path.join(__dirname, 'controller'),
    repositories: path.join(__dirname, 'repository'),
    services: path.join(__dirname, 'service'),
    serviceImpl: path.join(__dirname, 'service/impl')
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

import com.example.tileshop.domain.entity.${entity};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entity}Repository extends JpaRepository<${entity}, Long> {
}`;
}

// Nội dung Service
function getServiceContent(entity) {
    return `package com.example.tileshop.service;

import com.example.tileshop.domain.entity.${entity};

import java.util.List;

public interface ${entity}Service {
    List<${entity}> getAll();
}`;
}

// Nội dung ServiceImpl
function getServiceImplContent(entity) {
    return `package com.example.tileshop.service.impl;

import com.example.tileshop.domain.entity.${entity};
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
    });

    console.log('✅ Generation complete!');
}

generate();
