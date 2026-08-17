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
        try {
            CustomerResponse response=service.RegisterCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while inserting new customer :( ");
        }
    }
    @GetMapping
     public ResponseEntity<?> getallCustomers(@RequestParam(required = false ,defaultValue = "0") int page
     ,@RequestParam(required = false , defaultValue = "10") int size){
        try {
            Page<Customer> customers =service.findAllCustomers(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading all customer :( ");
        }
    }
    @DeleteMapping("/{id}")
       public ResponseEntity<?> deleteCustomer(@PathVariable(required = true) int id){
        try {
            CustomerResponse response=service.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while deleting customer :( ");
        }
    }
    @GetMapping("/{id}")
     public ResponseEntity<?> getById(@PathVariable(required = true) int id){
        try {
            CustomerResponse response=service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading customer :( ");
        }
    }
    @PutMapping("/{id}")
     public ResponseEntity<?> update(@PathVariable(required = true) int id,@RequestBody CustomerRequest request){
        try {
                CustomerResponse response=service.updateProfile(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("error happened while updating customer :( ");
        }
    }
      @GetMapping("/{id}/orders")
     public ResponseEntity<?> getCustomerOrdersHistory(@PathVariable(required = true) int id,@RequestParam(defaultValue = "0",required = false) int page,
     @RequestParam(defaultValue ="5",required = false) int size){
        try {
            Page <OrderResponseDto> response=orderService.CustomerOrdersHistory(id, page, size);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading customer orders :( ");
        }
    }

}
