package com.example.SpringDataJpa.resturantApp.Customer;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringDataJpa.resturantApp.Orders.OrderService;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerService service;
    private OrderService orderService;
    public CustomerController(CustomerService service,OrderService orderService){
        this.service=service;
        this.orderService=orderService;
    }
    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody(required = true) CustomerRequest request){
       
            CustomerResponse response=service.RegisterCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    }
    @GetMapping
     public ResponseEntity<?> getallCustomers(@RequestParam(required = false ,defaultValue = "0") int page
     ,@RequestParam(required = false , defaultValue = "10") int size){
       
            Page<Customer> customers =service.findAllCustomers(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
      
    }
    @DeleteMapping("/{id}")
       public ResponseEntity<?> deleteCustomer(@PathVariable(required = true) int id){
       
            CustomerResponse response=service.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
      
    }
    @GetMapping("/{id}")
     public ResponseEntity<?> getById(@PathVariable(required = true) int id){
       
            CustomerResponse response=service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
       
    }
    @PutMapping("/{id}")
     public ResponseEntity<?> update(@PathVariable(required = true) int id,@RequestBody CustomerRequest request){
        
                CustomerResponse response=service.updateProfile(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }
      @GetMapping("/{id}/orders")
     public ResponseEntity<?> getCustomerOrdersHistory(@PathVariable(required = true) int id,@RequestParam(defaultValue = "0",required = false) int page,
     @RequestParam(defaultValue ="5",required = false) int size){
        
            Page <OrderResponseDto> response=orderService.CustomerOrdersHistory(id, page, size);
            return ResponseEntity.status(HttpStatus.OK).body(response);
     }
    

}
