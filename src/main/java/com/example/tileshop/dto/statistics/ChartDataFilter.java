package com.example.tileshop.dto.statistics;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataFilter {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private LocalDate date;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    private String type;
}
