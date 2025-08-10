package com.kubertest.java.demo.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.kubertest.java.demo.data.entity.Customer;
import com.kubertest.java.demo.data.repository.CustomerRepository;
import com.kubertest.java.demo.util.BadRequestException;
import com.kubertest.java.demo.util.NotFoudException;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;




@RestController 
@RequestMapping("customers")
@Slf4j
public class CustomerController {
  private final CustomerRepository customerRepository;
  private final Map<String, Timer> timerMap;
  private static final String GET_ALL_CUSTOMERS = "getAllCustomers";
  private static final String ADD_CUSTOMER = "addCustomer";
  private static final String GET_CUSTOMER = "getCustomer";
  private static final String UPDATE_CUSTOMER = "updateCustomer";
  private static final String DELETE_CUSTOMER = "deleteCustomer";
  
  public CustomerController(CustomerRepository customerRepository, MeterRegistry registry) {
    super();
    this.customerRepository = customerRepository;
    log.info("CustomerController initialized with CustomerRepository");
    timerMap = new HashMap<>();
    timerMap.put(GET_ALL_CUSTOMERS, registry.timer(GET_ALL_CUSTOMERS));
    timerMap.put(ADD_CUSTOMER, registry.timer(ADD_CUSTOMER));
    timerMap.put(GET_CUSTOMER, registry.timer(GET_CUSTOMER));
    timerMap.put(UPDATE_CUSTOMER, registry.timer(UPDATE_CUSTOMER));
    timerMap.put(DELETE_CUSTOMER, registry.timer(DELETE_CUSTOMER));
  } 

  @GetMapping
  public Iterable<Customer> getAllCustomers(@RequestParam(required=false) String email) {
    Timer.Sample timer = Timer.start();

    if(email != null && StringUtils.isNotBlank(email)) { 
      List<Customer> customers = new ArrayList<>();
      Optional<Customer> customer = this.customerRepository.findByEmail(email);
          if(customer.isPresent()) {
              log.info("Found customer with email: {}", email);
              customers.add(customer.get());
              timerMap.get(GET_ALL_CUSTOMERS).record(()->timer.stop(timerMap.get(GET_ALL_CUSTOMERS)) );
              return customers;
            }
          else{
              log.warn("No customer found with email: {}", email);
          }
        }
        Iterable<Customer> allCustomers = this.customerRepository.findAll();
        timerMap.get(GET_ALL_CUSTOMERS).record(()->timer.stop(timerMap.get(GET_ALL_CUSTOMERS)) );
        log.info("Returning all customers");
        return allCustomers;
  }
  
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Customer createCustomer(@RequestBody Customer customer) {
      //TODO: process POST request
      log.info("Creating customer: {}", customer);
      return this.customerRepository.save(customer);
  }
  @PutMapping("/{id}")
  public Customer updateCustomer(@PathVariable UUID id, @RequestBody Customer customer) {
      if(id.equals(customer.getCustomerId())) {
          log.info("Updating customer with ID: {}", id);
          return this.customerRepository.save(customer);
      } else {
          log.warn("ID in path variable does not match ID in request body");
          throw new BadRequestException("ID in path variable does not match ID in request body");
      }
  }
  
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.RESET_CONTENT)
  public void deleteCustomer(@RequestParam UUID id) {
    if (customerRepository.existsById(id)) {
        customerRepository.deleteById(id);
        log.info("Deleted customer with ID: {}", id);
    } else {
        log.warn("Customer with ID {} not found", id);
    }
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Customer getMethodName(@PathVariable UUID id) {
      Optional<Customer> customer = customerRepository.findById(id);
      if(!customer.isPresent()) {
          throw new NotFoudException("Customer with ID " + id + " not found"); 
      } 
      return customer.get();
  }
  
}
