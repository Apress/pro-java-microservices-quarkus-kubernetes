package com.targa.labs.quarkushop.service;

import com.targa.labs.quarkushop.domain.Address;
import com.targa.labs.quarkushop.web.dto.AddressDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressServiceUnitTest {

    @Test
    void createFromDto() {
        Address address = new Address("413 Circle Drive", null, "Washington D.C.", "20010", "US");

        AddressDto addressDto = AddressService.mapToDto(address);

        assertThat(addressDto).isNotNull()
                .matches(dto -> dto.getAddress1().equals("413 Circle Drive"))
                .matches(dto -> dto.getCity().equals("Washington D.C."))
                .matches(dto -> dto.getPostcode().equals("20010"))
                .matches(dto -> dto.getCountry().equals("US"));
    }

    @Test
    void mapToDto() {
        AddressDto addressDto = new AddressDto("413 Circle Drive",
                null,
                "Washington D.C.",
                "20010",
                "US");

        Address mappedAddress = AddressService.createFromDto(addressDto);

        assertThat(mappedAddress).isNotNull()
                .matches(address -> address.getAddress1().equals("413 Circle Drive"))
                .matches(address -> address.getCity().equals("Washington D.C."))
                .matches(address -> address.getPostcode().equals("20010"))
                .matches(address -> address.getCountry().equals("US"));
    }
}