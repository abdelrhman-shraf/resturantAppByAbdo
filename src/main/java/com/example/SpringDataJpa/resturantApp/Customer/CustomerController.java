package com.example.SpringDataJpa.resturantApp.Customer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

import com.example.SpringDataJpa.resturantApp.Customer.Dto.CustomerRequest;
import com.example.SpringDataJpa.resturantApp.Customer.Dto.CustomerResponse;
import com.example.SpringDataJpa.resturantApp.Orders.OrderService;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.AppUserDetailsService;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.Appuser;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerService service;
    private OrderService orderService;
    private AppUserDetailsService userService;
    public CustomerController(CustomerService service,OrderService orderService,AppUserDetailsService userService){
        this.service=service;
        this.orderService=orderService;
        this.userService=userService;
    }
    @PostMapping("register")
    public ResponseEntity<?> registerCustomer(@RequestBody(required = true) CustomerRequest request){
       
            CustomerResponse response=service.RegisterCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    }
   @PreAuthorize ("hasRole('ADMIN')")
    @GetMapping
     public ResponseEntity<?> getallCustomers(@RequestParam(required = false ,defaultValue = "0") int page
     ,@RequestParam(required = false , defaultValue = "10") int size,@AuthenticationPrincipal Jwt jwt){
       List <String> roles = jwt.getClaimAsStringList("roles");
       roles.forEach(System.out::println);
            Page<Customer> customers =service.findAllCustomers(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
      
    }
    @PreAuthorize ("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
       public ResponseEntity<?> deleteCustomer(@PathVariable(required = true) int id){
       
            CustomerResponse response=service.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
      
    }
    @PreAuthorize ("hasRole('CUSTOMER')")
    @DeleteMapping("/me")
    public  ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Jwt jwt){
        Integer userId=Integer.valueOf(jwt.getSubject());
        service.deleteMyAccount(userId);
        return  ResponseEntity.noContent().build();
    }
    @PreAuthorize ("hasRole('ADMIN')")
    @GetMapping("/{id}")
     public ResponseEntity<?> getById(@PathVariable(required = true) int id){
       
            CustomerResponse response=service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
       
    }
    @PreAuthorize ("hasRole('CUSTOMER')")
    @GetMapping ("/me")
    public  ResponseEntity<CustomerResponse> getCustomer(@AuthenticationPrincipal  Jwt jwt){
        Integer userId=Integer.valueOf(jwt.getSubject());
        CustomerResponse response=service.getCustomer(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PreAuthorize ("@customerAuthorizationService.canUpdateCustomer(authentication,#id)")
    @PutMapping("/{id}")
     public ResponseEntity<?> update(@PathVariable(required = true) int id,@RequestBody CustomerRequest request){
        
                CustomerResponse response=service.updateProfile(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }
    @PreAuthorize ("hasRole('ADMIN')")
      @GetMapping("/{id}/orders")
     public ResponseEntity<?> getCustomerOrdersHistory(@PathVariable(required = true) Integer id,
     @RequestParam(defaultValue = "0",required = false) int page,
     @RequestParam(defaultValue ="5",required = false) int size){
          
            Page <OrderResponseDto> response=orderService.CustomerOrdersHistory(id, page, size);
            return ResponseEntity.status(HttpStatus.OK).body(response);
     }
@PreAuthorize("hasRole('CUSTOMER')")
@GetMapping("/me/orders")
public ResponseEntity<?> getMyOrders(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Integer userId = Integer.valueOf(jwt.getSubject());
    Appuser user = userService.loadUserById(userId);

    return ResponseEntity.ok(
            orderService.CustomerOrdersHistory(
                    user.getCustomerId(), page, size));
}

}
