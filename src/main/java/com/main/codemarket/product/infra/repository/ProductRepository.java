package com.main.codemarket.product.infra.repository;

import com.main.codemarket.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
