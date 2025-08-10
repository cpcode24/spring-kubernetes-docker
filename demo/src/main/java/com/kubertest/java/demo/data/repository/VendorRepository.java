package com.kubertest.java.demo.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kubertest.java.demo.data.entity.Vendor;


public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    // Additional query methods can be defined here if needed
  List<Vendor> findByName(String name);
}
