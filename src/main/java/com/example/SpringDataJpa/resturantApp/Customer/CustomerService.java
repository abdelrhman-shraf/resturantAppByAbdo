package com.example.SpringDataJpa.resturantApp.Customer;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class CustomerService{
   private CustomerRepo repo;
   @Autowired
   private CustomerRequestMapper requestmapper;
@Autowired
public CustomerService(CustomerRepo repo){
    this.repo=repo;
}
private CustomerResponse toCustomerResponse(Customer c){
    return new CustomerResponse(c.getCustomerId(),c.getFirstName(),c.getLastName()
    , c.getPhone(), c.getEmail(), c.getAddress());
} 

public CustomerResponse RegisterCustomer(CustomerRequest request){
Customer c=new Customer();
c.setFirstName(request.getFirstName());
c.setLastName(request.getLastName());
c.setPhone(request.getPhone());
c.setEmail(request.getEmail());
c.setAddress(request.getAddress());
Customer saved=repo.save(c);
return toCustomerResponse(saved);
    
}
public Page<Customer> findAllCustomers(int page,int size){
Pageable p=PageRequest.of(page, size);
return repo.findAll(p);
}
public CustomerResponse deleteCustomer(int id){
    Customer c=repo.findById(id)
    .orElseThrow(()-> new EntityNotFoundException("Customer not found for id : " + id));
    repo.deleteById(id);
    CustomerResponse deleted=toCustomerResponse(c);
    return deleted;
}
public CustomerResponse getById(int id){
    Customer c = repo.findById(id)
    .orElseThrow(()-> new EntityNotFoundException("Customer not found for id : " + id));;
    return toCustomerResponse(c);
}
public CustomerResponse updateProfile(int id,CustomerRequest request){
Customer customer=repo.findById(id)
.orElseThrow(()-> new EntityNotFoundException("Customer not found for id : " + id));
    requestmapper.updateCustomerFromDto(request,customer);
    Customer saved=repo.save(customer);
    return toCustomerResponse(saved);
}



}