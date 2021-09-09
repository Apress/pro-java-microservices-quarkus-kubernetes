package com.targa.labs.quarkushop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * @author Nebrass Lamouchi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private BigDecimal totalPrice;
    private String status;
    private ZonedDateTime shipped;
    private Long paymentId;
    private AddressDto shipmentAddress;
    private Set<OrderItemDto> orderItems;
    private CartDto cart;
}
