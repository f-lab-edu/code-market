package com.main.codemarket.product.application.service;

import com.main.codemarket.product.domain.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProducts();

    Product findProduct(long id);

    Product saveProduct(Product product);
}
