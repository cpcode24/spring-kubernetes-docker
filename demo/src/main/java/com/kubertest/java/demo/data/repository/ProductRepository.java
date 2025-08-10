package com.kubertest.java.demo.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kubertest.java.demo.data.entity.Product;


public interface ProductRepository extends JpaRepository<Product, UUID>{
  List<Product> findByName(String name);
}
