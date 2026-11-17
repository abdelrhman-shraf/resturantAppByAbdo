package com.example.SpringDataJpa.resturantApp.Customer;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringDataJpa.resturantApp.CustomExceptions.DuplicateResourceException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;
import com.example.SpringDataJpa.resturantApp.Customer.Dto.CustomerRequest;
import com.example.SpringDataJpa.resturantApp.Customer.Dto.CustomerResponse;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.AppUserDetailsService;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.Appuser;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.CustomUserDetails;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.UserRepository;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.appRole.AppRole;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.appRole.AppRoleRepository;


@Service
@Transactional(readOnly = true)
public class CustomerService{
   private CustomerRepo repo;
   private CustomerRequestMapper requestmapper;
   private  PasswordEncoder passwordEncoder;
   private AppRoleRepository appRoleRepository;
   private  UserRepository userRepository;
   private AppUserDetailsService userService;
   
@Autowired
public CustomerService(CustomerRepo repo,CustomerRequestMapper requestmapper,
    PasswordEncoder passwordEncoder,AppRoleRepository appRoleRepository,
     UserRepository userRepository,AppUserDetailsService userService){
    this.repo=repo;
    this.requestmapper=requestmapper;
    this.passwordEncoder=passwordEncoder;
    this.appRoleRepository=appRoleRepository;
    this.userRepository=userRepository;
    this.userService=userService;
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
// add user security
Appuser user=new Appuser();
user.setCustomerId(saved.getCustomerId());
user.setUsername(saved.getEmail());
user.setPassword(passwordEncoder.encode(request.getPassword()));
AppRole role = appRoleRepository.findByname("ROLE_CUSTOMER")
.orElseThrow(()-> new EntityNotFoundException("role with  name :" + "ROLE_CUSTOMER" + " doesn't exist :( "));

user.getRoles().add(role);
userRepository.save(user);

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
public  CustomerResponse getCustomer(Integer userId){
   Appuser user=userService.loadUserById(userId);
   return getById(user.getCustomerId());
}
@Transactional
public CustomerResponse updateProfile(int id,CustomerRequest request){
Customer customer=repo.findById(id)
.orElseThrow(()-> new ResourceNotFoundException("customer", "customerId", id));
    requestmapper.updateCustomerFromDto(request,customer);
    Customer saved=repo.save(customer);
    return toCustomerResponse(saved);
}
@Transactional 
public void deleteMyAccount(Integer userId) {
Appuser user = userRepository.findById(userId).orElseThrow(()->
 new EntityNotFoundException());
 Integer customerId = user.getCustomerId();

    if (customerId == null) {
        throw new EntityNotFoundException("Customer not associated with this user");
    }
      Customer customer = repo.findById(customerId)
            .orElseThrow(() ->
                    new EntityNotFoundException("Customer not found"));
 repo.delete(customer);
 userRepository.delete(user);
 
}



}