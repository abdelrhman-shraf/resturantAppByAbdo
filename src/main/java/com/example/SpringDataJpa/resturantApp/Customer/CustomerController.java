package com.example.SpringDataJpa.resturantApp.Customer;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerService service;
    public CustomerController(CustomerService service){
        this.service=service;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@ RequestBody CustomerRequest request){
        try {
            CustomerResponse response=service.RegisterCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while inserting new customer :( ");
        }
    }
    @GetMapping("/getall")
     public ResponseEntity<?> getallCustomers(@RequestParam int page,@RequestParam int size){
        try {
            Page<Customer> customers =service.findAllCustomers(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading all customer :( ");
        }
    }
    @PutMapping("/delete")
       public ResponseEntity<?> deleteCustomer(@RequestParam int id){
        try {
            CustomerResponse response=service.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while deleting customer :( ");
        }
    }
    @GetMapping("/getbyid")
     public ResponseEntity<?> getById(@RequestParam int id){
        try {
            CustomerResponse response=service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading customer :( ");
        }
    }
    @PutMapping("updateProfile")
     public ResponseEntity<?> update(@RequestBody CustomerRequest request,@RequestParam int id){
        try {
                CustomerResponse response=service.updateProfile(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("error happened while updating customer :( ");
        }
    }

}
