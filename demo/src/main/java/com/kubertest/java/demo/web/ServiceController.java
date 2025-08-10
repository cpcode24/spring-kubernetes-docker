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

import com.kubertest.java.demo.data.entity.Service;
import com.kubertest.java.demo.data.repository.ServiceRepository;
import com.kubertest.java.demo.util.BadRequestException;
import com.kubertest.java.demo.util.NotFoudException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/services")
@Slf4j
public class ServiceController {
  private final ServiceRepository serviceRepository;

  public ServiceController(ServiceRepository serviceRepository) {
    super();
    this.serviceRepository = serviceRepository;
  } 
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Service createService(@RequestBody Service service) {
    //TODO: process POST request
    log.info("Creating service: {}", service);
    return serviceRepository.save(service);
    
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.RESET_CONTENT)
public void deleteService(@RequestParam UUID id) {
    if (serviceRepository.existsById(id)) {
        serviceRepository.deleteById(id);
        log.info("Deleted service with ID: {}", id);
    } else {
        throw new NotFoudException("Service with ID " + id + " not found");
    }
}

@PutMapping("/{id}")
public Service updateService(@PathVariable String id, @RequestBody Service service) {
    //TODO: process PUT request
    if(id.equals(service.getServiceId().toString())) {
        log.info("Updating service with ID: {}", id);
        return serviceRepository.save(service);
    } else {
        log.warn("ID in path variable does not match ID in request body");
        throw new BadRequestException("ID in path variable does not match ID in request body");
    }
}  
  @GetMapping
  public List<Service> getAllServices(@RequestParam(required = false) String name) {
    if (name != null && !name.isBlank()) {
      List<Service> services = this.serviceRepository.findByName(name);
      if (!services.isEmpty()) {
        log.info("Found services with name: {}", name);
        return services;
      } else {
        log.warn("No service found with name: {}", name);
      }
      return services;
    }
    return this.serviceRepository.findAll();
  }

  @GetMapping("/{id}")
  public  Service getService(@PathVariable UUID id) {
      Optional<Service> service = serviceRepository.findById(id);
      if(service.isPresent()) {
          log.info("Found service with ID: {}", id);
          return service.get();
      } else {
          log.warn("No service found with ID: {}", id);
          throw new NotFoudException("Service with ID " + id + " not found"); 
    }
  }
}
