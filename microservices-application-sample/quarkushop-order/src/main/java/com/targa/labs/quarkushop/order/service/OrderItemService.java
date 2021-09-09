package com.targa.labs.quarkushop.order.service;

import com.targa.labs.quarkushop.order.client.ProductRestClient;
import com.targa.labs.quarkushop.order.domain.OrderItem;
import com.targa.labs.quarkushop.order.dto.OrderItemDto;
import com.targa.labs.quarkushop.order.repository.OrderItemRepository;
import com.targa.labs.quarkushop.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class OrderItemService {

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    OrderRepository orderRepository;

    @RestClient
    ProductRestClient productRestClient;

    public static OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getQuantity(),
                orderItem.getProductId(),
                orderItem.getOrder().getId()
        );
    }

    public OrderItemDto findById(Long id) {
        log.debug("Request to get OrderItem : {}", id);
        return this.orderItemRepository.findById(id).map(OrderItemService::mapToDto).orElse(null);
    }

    public OrderItemDto create(OrderItemDto orderItemDto) {
        log.debug("Request to create OrderItem : {}", orderItemDto);
        var order = this.orderRepository
                .findById(orderItemDto.getOrderId())
                .orElseThrow(() -> new IllegalStateException("The Order does not exist!"));

        var orderItem = this.orderItemRepository.save(
                new OrderItem(
                        orderItemDto.getQuantity(),
                        orderItemDto.getProductId(),
                        order
                ));

        var product = this.productRestClient.findById(orderItem.getProductId());

        order.setPrice(order.getPrice().add(product.getPrice()));
        this.orderRepository.save(order);

        return mapToDto(orderItem);
    }

    public void delete(Long id) {
        log.debug("Request to delete OrderItem : {}", id);

        var orderItem = this.orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("The OrderItem does not exist!"));

        var order = orderItem.getOrder();
        var product = this.productRestClient.findById(orderItem.getProductId());

        order.setPrice(order.getPrice().subtract(product.getPrice()));

        this.orderItemRepository.deleteById(id);

        order.getOrderItems().remove(orderItem);
        this.orderRepository.save(order);
    }

    public List<OrderItemDto> findByOrderId(Long id) {
        log.debug("Request to get all OrderItems of OrderId {}", id);
        return this.orderItemRepository.findAllByOrderId(id)
                .stream()
                .map(OrderItemService::mapToDto)
                .collect(Collectors.toList());
    }
}
