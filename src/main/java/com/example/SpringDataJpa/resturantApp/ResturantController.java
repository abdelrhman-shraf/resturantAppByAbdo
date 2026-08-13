package com.example.SpringDataJpa.resturantApp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SpringDataJpa.resturantApp.Customer.CustomerRepo;
import com.example.SpringDataJpa.resturantApp.Customer.Customer;

@RestController
@RequestMapping("/main")
public class ResturantController {
    private CustomerRepo repo;
    @Autowired
    public ResturantController(CustomerRepo repo){
        this.repo=repo;
    }
    @PostMapping("/addCustomer")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer){
        try {
            repo.save(customer);
            return ResponseEntity.status(HttpStatusCode.valueOf(202)).body(customer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("error happened while inserting new customer :( ");
        }
    }
    @GetMapping("/getallCustomers")
    public ResponseEntity<?> getallCustomers(){
        try {
            List<Customer>customers= repo.findAll();
            return ResponseEntity.status(HttpStatusCode.valueOf(202)).body(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("error happened while getting all customers :( ");
        }
    }
}
