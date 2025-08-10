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

import com.kubertest.java.demo.data.entity.Vendor;
import com.kubertest.java.demo.data.repository.VendorRepository;
import com.kubertest.java.demo.util.BadRequestException;
import com.kubertest.java.demo.util.NotFoudException;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("vendors")
@Slf4j
public class VendorController {
  private final VendorRepository vendorRepository;

  public VendorController(VendorRepository vendorRepository) {
    super();
    this.vendorRepository = vendorRepository;
    log.info("VendorController initialized with VendorRepository");
  } 

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Vendor createVendor(@RequestBody Vendor vendor) {
    log.info("Creating vendor: {}", vendor);
    return vendorRepository.save(vendor);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Vendor getVendorById(@PathVariable UUID id) {
    log.info("Fetching vendor with ID: {}", id);
    Optional<Vendor> vendor = vendorRepository.findById(id);
    if (vendor.isPresent()) {
      return vendor.get();
    } else {
      log.warn("Vendor with ID {} not found", id);
      throw new NotFoudException("Vendor with ID " + id + " not found");
    } 
  }

  @PutMapping("/{id}")    
  public Vendor updateVendor(@RequestParam UUID id, @RequestBody Vendor vendor) {
      if(id.equals(vendor.getVendorId())) {
          log.info("Updating vendor with ID: {}", id);
          return vendorRepository.save(vendor);
      } else {
          log.warn("ID in path variable does not match ID in request body");
          throw new BadRequestException("ID in path variable does not match ID in request body");
      } 
  }

  @GetMapping
  public List<Vendor> getAllVendors(@RequestParam(required=false) String name) {
    if (name != null && !name.isBlank()) {
      List<Vendor> vendors =  vendorRepository.findByName(name);
      if (!vendors.isEmpty()) {
        log.info("Found vendors with name: {}", name);
        return vendors;
      }
    }
    return vendorRepository.findAll();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.RESET_CONTENT)
  public void deleteVendor(@RequestParam UUID id) {
    if (vendorRepository.existsById(id)) {
      vendorRepository.deleteById(id);
      log.info("Deleted vendor with ID: {}", id);
    } else {
      log.warn("Vendor with ID {} not found", id);
      throw new NotFoudException("Vendor with ID " + id + " not found");      
    }
  }
  
}
