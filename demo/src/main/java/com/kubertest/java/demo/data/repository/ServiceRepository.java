package com.kubertest.java.demo.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kubertest.java.demo.data.entity.Service;


public interface ServiceRepository extends JpaRepository<Service, UUID>{
  List<Service> findByName(String name);
}
