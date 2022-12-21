package be.rubus.microstream.spring.example.controller;

import be.rubus.microstream.spring.example.database.Products;
import be.rubus.microstream.spring.example.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class ProductController {

    private final Products redRoot;

    public ProductController(Products redRoot) {
        this.redRoot = redRoot;
    }

    @GetMapping("/products")
    public Collection<Product> getAll() {
        return redRoot.getProducts();
    }

    @PostMapping("/products")
    public void addProduct(@RequestBody Product newProduct) {
        redRoot.addProduct(newProduct);
    }

    @DeleteMapping("/product/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        redRoot.deleteProduct(productId);
    }

}
