package com.targa.labs.quarkushop.customer.repository;

import com.targa.labs.quarkushop.customer.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
