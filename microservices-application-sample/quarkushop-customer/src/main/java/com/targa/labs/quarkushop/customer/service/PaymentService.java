package com.targa.labs.quarkushop.customer.service;

import com.targa.labs.quarkushop.customer.client.OrderRestClient;
import com.targa.labs.quarkushop.customer.domain.Payment;
import com.targa.labs.quarkushop.customer.domain.enums.PaymentStatus;
import com.targa.labs.quarkushop.customer.dto.OrderDto;
import com.targa.labs.quarkushop.customer.dto.PaymentDto;
import com.targa.labs.quarkushop.customer.repository.PaymentRepository;
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
public class PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    @RestClient
    OrderRestClient orderRestClient;

    public static PaymentDto mapToDto(Payment payment, Long orderId) {
        if (payment != null) {
            return new PaymentDto(
                    payment.getId(),
                    payment.getTransaction(),
                    payment.getStatus().name(),
                    orderId
            );
        }
        return null;
    }

    public List<PaymentDto> findAll() {
        log.debug("Request to get all Payments");
        return this.paymentRepository.findAll()
                .stream()
                .map(payment -> findById(payment.getId()))
                .collect(Collectors.toList());
    }

    public PaymentDto findById(Long id) {
        log.debug("Request to get Payment : {}", id);
        var order = findOrderByPaymentId(id);

        return this.paymentRepository
                .findById(id)
                .map(payment -> mapToDto(payment, order.getId()))
                .orElse(null);
    }

    public PaymentDto create(PaymentDto paymentDto) {
        log.debug("Request to create Payment : {}", paymentDto);

        OrderDto order =
                this.orderRestClient
                        .findById(paymentDto.getOrderId())
                        .orElseThrow(() -> new IllegalStateException("The Order does not exist!"));

        order.setStatus("PAID");

        var payment = this.paymentRepository.save(new Payment(
                paymentDto.getTransaction(),
                PaymentStatus.valueOf(paymentDto.getStatus()),
                order.getTotalPrice()
        ));

        this.orderRestClient.save(order);
        return mapToDto(payment, order.getId());
    }

    private OrderDto findOrderByPaymentId(Long id) {
        return this.orderRestClient.findByPaymentId(id)
                .orElseThrow(() -> new IllegalStateException("No Order exists for the Payment ID " + id));
    }

    public void delete(Long id) {
        log.debug("Request to delete Payment : {}", id);
        this.paymentRepository.deleteById(id);
    }
}