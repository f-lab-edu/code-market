package com.main.codemarket.product.application.service;

import com.main.codemarket.product.domain.entity.Product;
import com.main.codemarket.product.infra.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProduct(long id) {
        return productRepository.findById(id)
                .map(product -> Product.createProduct(
                        product.getProductName(), product.getStock(), product.getPrice()))
                .orElseThrow();
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
