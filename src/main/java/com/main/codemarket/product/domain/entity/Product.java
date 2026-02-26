package com.main.codemarket.product.domain.entity;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int price;

    protected Product() {
    }

    public static Product createProduct(String productName, int stock, int price) {
        Product product = new Product();
        product.productName = productName;
        product.stock = stock;
        product.price = price;
        return product;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getStock() {
        return stock;
    }

    public int getPrice() {
        return price;
    }
}
