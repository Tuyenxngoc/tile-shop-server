package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.VisitRequest;
import com.example.tileshop.service.VisitTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Visit")
public class VisitController {
    VisitTrackingService visitTrackingService;

    @Operation(summary = "API Track Visit")
    @PostMapping(UrlConstant.Visit.TRACK)
    public ResponseEntity<Void> trackVisit(@RequestBody VisitRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        visitTrackingService.trackVisit(request.getUrl(), ip);
        return ResponseEntity.ok().build();
    }
}