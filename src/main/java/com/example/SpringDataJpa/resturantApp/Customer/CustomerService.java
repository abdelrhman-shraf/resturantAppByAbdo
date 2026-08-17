package com.example.SpringDataJpa.resturantApp.Customer;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringDataJpa.resturantApp.CustomExceptions.DuplicateResourceException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;


@Service
@Transactional(readOnly = true)
public class CustomerService{
   private CustomerRepo repo;
   private CustomerRequestMapper requestmapper;
   
@Autowired
public CustomerService(CustomerRepo repo,CustomerRequestMapper requestmapper){
    this.repo=repo;
    this.requestmapper=requestmapper;
}
private CustomerResponse toCustomerResponse(Customer c){
    return new CustomerResponse(c.getCustomerId(),c.getFirstName(),c.getLastName()
    , c.getPhone(), c.getEmail(), c.getAddress());
} 
@Transactional
public CustomerResponse RegisterCustomer(CustomerRequest request){
    if (repo.existsByEmail(request.getEmail())) {
        throw new DuplicateResourceException("customer", "email", request.getEmail());
    }
Customer c=new Customer();
c.setFirstName(request.getFirstName());
c.setLastName(request.getLastName());
c.setPhone(request.getPhone());
c.setEmail(request.getEmail());
c.setAddress(request.getAddress());
Customer saved=repo.save(c);
return toCustomerResponse(saved);
    
}
@Transactional(isolation = Isolation.READ_COMMITTED)
public Page<Customer> findAllCustomers(int page,int size){
Pageable p=PageRequest.of(page, size);
return repo.findAll(p);
}
@Transactional
public CustomerResponse deleteCustomer(int id){
    Customer c=repo.findById(id)
    .orElseThrow(()-> new ResourceNotFoundException("customer", "customerId", id));
    repo.deleteById(id);
    CustomerResponse deleted=toCustomerResponse(c);
    return deleted;
}
public CustomerResponse getById(int id){
    Customer c = repo.findById(id)
    .orElseThrow(()-> new ResourceNotFoundException("customer", "customerId", id));;
    return toCustomerResponse(c);
}
@Transactional
public CustomerResponse updateProfile(int id,CustomerRequest request){
Customer customer=repo.findById(id)
.orElseThrow(()-> new ResourceNotFoundException("customer", "customerId", id));
    requestmapper.updateCustomerFromDto(request,customer);
    Customer saved=repo.save(customer);
    return toCustomerResponse(saved);
}



}