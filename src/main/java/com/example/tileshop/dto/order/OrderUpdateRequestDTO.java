package com.example.tileshop.dto.order;

import com.example.tileshop.config.TrimStringDeserializer;
import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.DeliveryMethod;
import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDTO {
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private DeliveryMethod deliveryMethod;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_FULL_NAME, message = ErrorMessage.INVALID_FORMAT_NAME)
    @Size(min = 2, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String recipientName;

    private Gender recipientGender;

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String recipientEmail;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_PHONE_NUMBER, message = ErrorMessage.INVALID_FORMAT_PHONE)
    @Size(min = 10, max = 20, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String recipientPhone;

    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String shippingAddress;

    @Size(max = 512, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String note;

    @Size(max = 512, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String cancelReason;

    @AssertTrue(message = ErrorMessage.Order.ERR_MISSING_SHIPPING_ADDRESS)
    @JsonIgnore
    public boolean isShippingAddressValid() {
        if (DeliveryMethod.HOME_DELIVERY.equals(deliveryMethod)) {
            return isNotBlank(shippingAddress);
        }
        return true;
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
