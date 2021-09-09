package com.targa.labs.quarkushop.order.repository;

import com.targa.labs.quarkushop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCartCustomer(Long customer);

    Optional<Order> findByPaymentId(Long id);
}
