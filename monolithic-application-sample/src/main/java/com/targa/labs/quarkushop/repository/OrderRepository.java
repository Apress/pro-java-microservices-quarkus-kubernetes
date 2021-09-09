package com.targa.labs.quarkushop.repository;

import com.targa.labs.quarkushop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCartCustomerId(Long customerId);

    Optional<Order> findByPaymentId(Long id);
}
