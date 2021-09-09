package com.targa.labs.quarkushop.customer.client;

import com.targa.labs.quarkushop.customer.dto.OrderDto;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Optional;

@Mock
@ApplicationScoped
@RestClient
public class MockOrderRestClient implements OrderRestClient {

    @Override
    public Optional<OrderDto> findById(Long id) {
        OrderDto order = new OrderDto();
        order.setId(id);
        order.setTotalPrice(BigDecimal.valueOf(1000));
        return Optional.of(order);
    }

    @Override
    public Optional<OrderDto> findByPaymentId(Long id) {
        OrderDto order = new OrderDto();
        order.setId(5L);
        return Optional.of(order);
    }

    @Override
    public OrderDto save(OrderDto order) {
        return order;
    }
}
