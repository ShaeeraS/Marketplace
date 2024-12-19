package com.makers.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByUserId(Long userId) {
        return null;
    }
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    //Users products:
    public List<Product> findProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    //Delete products:
    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }

    public boolean existsById(Long productId) {
        return productRepository.existsById(productId);
    }

    //Edit products:
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    //Search products:
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}
