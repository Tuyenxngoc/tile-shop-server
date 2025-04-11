package com.example.tileshop.dto.customer;

import com.example.tileshop.constant.Gender;
import com.example.tileshop.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponseDTO {
	private long id;

	private String fullName;

	private String phoneNumber;

	private String address;

	private Gender gender;

	public CustomerResponseDTO(Customer customer) {
		this.id = customer.getId();
		this.fullName = customer.getFullName();
		this.phoneNumber = customer.getPhoneNumber();
		this.address = customer.getAddress();
		this.gender = customer.getGender();
	}
}
