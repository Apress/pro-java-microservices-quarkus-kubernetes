package com.targa.labs.quarkushop.repository;

import com.targa.labs.quarkushop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    Long countAllByCategoryId(Long categoryId);

    @Query("select p from Product p JOIN p.reviews r WHERE r.id = ?1")
    Product findProductByReviewId(Long reviewId);

    void deleteAllByCategoryId(Long id);

    List<Product> findAllByCategoryId(Long id);
}
