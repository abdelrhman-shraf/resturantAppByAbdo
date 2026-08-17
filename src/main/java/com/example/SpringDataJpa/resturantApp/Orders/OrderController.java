package com.example.SpringDataJpa.resturantApp.Orders;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Projections.MostLoyalCustomerProjection;
import com.example.SpringDataJpa.resturantApp.Orders.dto.CreateOrderDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderInfoResponse;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.TopSellingItemDto;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService service;
    @Autowired
    public OrderController(OrderService service){
        this.service=service;
    }
    public record ChangeStatusRequestBody(
        String status
    ) {
    }
    @PostMapping
        public ResponseEntity<?> addOrder(@RequestBody CreateOrderDto request){
        
            OrderResponseDto response=service.createOrder(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
       
    }
    @PatchMapping("/cancel/{id}")
      public ResponseEntity<?> cancelOrder(@PathVariable Integer id){
        
            OrderResponseDto response=service.cancelOreder(id);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
       
    }
    @PutMapping("/{id}")
       public ResponseEntity<?> changeStatus(@PathVariable(required = true) Integer id,@RequestBody(required = true) ChangeStatusRequestBody status){
        
            OrderResponseDto response=service.changeStatus(id, StatusEnum.fromString(status.status()));
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
       
    }
    @GetMapping("/{id}")
       public ResponseEntity<?> getOrderById(@PathVariable(required = true) Integer id){
       
            OrderInfoResponse response=service.getOrderById(id);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
      
    }
    @GetMapping
      public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0",required = false) int page,@RequestParam(defaultValue ="5",required = false) int size){
        
            Page <OrderResponseDto> response=service.getAllOrders(page, size);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }
  
    @GetMapping("/top-selling")
     public ResponseEntity<?> getTopSelling(@RequestParam(required = false,defaultValue = "TIMESORDERD") String orderBy){
        
            Page <TopSellingItemDto> response=service.topSelling(orderBy);
            return ResponseEntity.status(HttpStatus.OK).body(response);
       

    }
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestParam(required = false,defaultValue = "week") String duration){
        
            BigDecimal response=service.getRevenue(duration);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
      

    }
    @GetMapping("/loyal")
    public ResponseEntity<?> getLoyalCustomers(@RequestParam(defaultValue = "3",required = false) Integer limit ,
    @RequestParam(defaultValue = "orders",required = false) String sort){
        
           List<MostLoyalCustomerProjection> response=service.getLoyalCustomers(limit, sort);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
       

    }

}
