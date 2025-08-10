package com.kubertest.java.demo.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kubertest.java.demo.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findById(UUID id);

    void deleteById(UUID id);

    void delete(Customer customer);

    boolean existsByEmail(String email);

}
