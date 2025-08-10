package com.kubertest.java.demo.data.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vendors")
@Data
public class Vendor {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
  private UUID vendorId;

  private String contact;
  @Column(unique = true)
  private String email;
  
  private String phone;
  private String address;

  @Column(nullable = false)
  private String name;
  
}
