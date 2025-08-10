package com.kubertest.java.demo.web;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kubertest.java.demo.data.entity.Product;
import com.kubertest.java.demo.data.repository.ProductRepository;
import com.kubertest.java.demo.util.BadRequestException;
import com.kubertest.java.demo.util.NotFoudException;

import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("products")
@Slf4j

public class ProductController {
  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    super();
    this.productRepository = productRepository;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product createProduct(@RequestBody Product product) {
      //TODO: process POST request
      log.info("Created product: {}", product);
      return productRepository.save(product);
  }
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Product getProduct(@PathVariable UUID id) {
      Optional<Product> product = productRepository.findById(id);
      if(product.isPresent()) {
          log.info("Found product with ID: {}", id);
          return product.get();
      } else {
          log.warn("No product found with ID: {}", id);
          throw new NotFoudException("Product with ID " + id + " not found"); 
      }
  }
  

  @PutMapping("/{id}") 
  public Product updateProduct(@RequestParam UUID id, @RequestBody Product product) {
      if(id.equals(product.getProductId())) {
          log.info("Updating product with ID: {}", id);
          return productRepository.save(product);
      } else {
          log.warn("ID in path variable does not match ID in request body");
          throw new BadRequestException("ID in path variable does not match ID in request body");
      } 
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.RESET_CONTENT)
  public void deleteProduct(@RequestParam UUID id) {  
    Optional<Product> product = productRepository.findById(id);
    if(product.isPresent()) {
      productRepository.deleteById(id);
      log.info("Deleted product with ID: {}", id);
    } else {
      throw new NotFoudException("Product with ID " + id + " not found");
    }
  }

  @GetMapping
  public List<Product> getAllProducts(@RequestParam(required=false) String name) {
    if(name != null && !name.isBlank()) {
      List<Product> products = this.productRepository.findByName(name);
      if(!products.isEmpty()) {
        log.info("Found products with name: {}", name);
        return products;
      } else {
        log.warn("No product found with name: {}", name);
      }
      return products;
    }
    return this.productRepository.findAll();
  }
}
