package com.example.tileshop.util;

import com.example.tileshop.dto.common.RestData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class VsResponseUtil {
    public static ResponseEntity<RestData<?>> success(Object data) {
        return success(HttpStatus.OK, data);
    }

    public static ResponseEntity<RestData<?>> success(HttpStatus status, Object data) {
        RestData<?> response = new RestData<>(data);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<RestData<?>> success(HttpStatus status, MultiValueMap<String, String> header, Object data) {
        RestData<?> response = new RestData<>(data);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll(header);
        return ResponseEntity.status(status).headers(responseHeaders).body(response);
    }

    public static ResponseEntity<RestData<?>> error(HttpStatus status, Object message) {
        RestData<?> response = RestData.error(message);
        return new ResponseEntity<>(response, status);
    }
}
