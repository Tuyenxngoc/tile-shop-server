package com.example.tileshop.dto.order;

import com.example.tileshop.config.TrimStringDeserializer;
import com.example.tileshop.constant.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Gender gender;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_FULL_NAME, message = ErrorMessage.INVALID_FORMAT_NAME)
    @Size(min = 2, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String fullName;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_PHONE_NUMBER, message = ErrorMessage.INVALID_FORMAT_PHONE)
    @Size(min = 10, max = 20, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String phoneNumber;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
    @Size(min = 5, max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String email;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private DeliveryMethod deliveryMethod;

    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String shippingAddress;

    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String note;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private PaymentMethod paymentMethod;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Boolean requestInvoice;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String invoiceCompanyName;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String invoiceTaxCode;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String invoiceCompanyAddress;

    @AssertTrue(message = ErrorMessage.Order.ERR_MISSING_INVOICE_INFO)
    @JsonIgnore
    public boolean isInvoiceInfoValid() {
        if (Boolean.TRUE.equals(requestInvoice)) {
            return isNotBlank(invoiceCompanyName) &&
                    isNotBlank(invoiceTaxCode) &&
                    isNotBlank(invoiceCompanyAddress);
        }
        return true;
    }

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
