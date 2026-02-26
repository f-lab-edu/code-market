package com.main.codemarket.product.ui.dto;

import com.main.codemarket.product.domain.entity.Product;

public class ProductCreateRequestDto {
    private String productName;

    private int stock;

    private int price;

    public ProductCreateRequestDto() {
    }

    public static Product createProductEntity(ProductCreateRequestDto productCreateRequestDto) {
        return Product.createProduct(productCreateRequestDto.getProductName(), productCreateRequestDto.getStock(), productCreateRequestDto.getPrice());
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
