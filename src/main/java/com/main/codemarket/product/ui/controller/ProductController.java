package com.main.codemarket.product.ui.controller;

import com.main.codemarket.product.application.service.ProductService;
import com.main.codemarket.product.domain.entity.Product;
import com.main.codemarket.product.ui.dto.ProductCreateRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품 목록 검색
     */
    @GetMapping()
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.findProducts());

    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.findProduct(id));
    }

    /**
     * 상품 등록
     */
    @PostMapping("")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto) {
        Product product = productService.saveProduct(ProductCreateRequestDto.createProductEntity(productCreateRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
