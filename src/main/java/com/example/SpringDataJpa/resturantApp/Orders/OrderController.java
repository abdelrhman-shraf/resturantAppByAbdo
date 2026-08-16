package com.example.SpringDataJpa.resturantApp.Orders;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    @PostMapping("/makeOrder")
        public ResponseEntity<?> addOrder(@RequestBody CreateOrderDto request){
        try {
            OrderResponseDto response=service.createOrder(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while creating an order :( ");
        }
    }
    @PutMapping("/cancel")
      public ResponseEntity<?> cancelOrder(@RequestParam Integer id){
        try {
            OrderResponseDto response=service.cancelOreder(id);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while canceling an order :( ");
        }
    }
    @PutMapping("/changestatus")
       public ResponseEntity<?> chanheStatus(@RequestParam(required = true) Integer id,@RequestParam(required = true) String statusEnum){
        try {
            OrderResponseDto response=service.changeStatus(id, StatusEnum.fromString(statusEnum));
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while changing status for an order :( ");
        }
    }
    @GetMapping("/get_order_by_id")
       public ResponseEntity<?> getOrderById(@RequestParam(required = true) Integer id){
        try {
            OrderInfoResponse response=service.getOrderById(id);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while getting order  :( ");
        }
    }
    @GetMapping("/getall")
      public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0",required = false) int page,@RequestParam(defaultValue ="5",required = false) int size){
        try {
            Page <OrderResponseDto> response=service.getAllOrders(page, size);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading all orders :( ");
        }
    }
    @GetMapping("/customer/history")
     public ResponseEntity<?> getCustomerOrdersHistory(@RequestParam(defaultValue = "0",required = false) int page,
     @RequestParam(defaultValue ="5",required = false) int size,@RequestParam(required = true) int customerId){
        try {
            Page <OrderResponseDto> response=service.CustomerOrdersHistory(customerId, page, size);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading customer orders :( ");
        }
    }
    @GetMapping("/get_top_selling")
     public ResponseEntity<?> getTopSelling(@RequestParam(required = false,defaultValue = "TIMESORDERD") String orderBy){
        try {
            Page <TopSellingItemDto> response=service.topSelling(orderBy);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while getting top seller :( ");
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while getting top seller :( ");
        }

    }
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestParam(required = false,defaultValue = "week") String duration){
        try {
            BigDecimal response=service.getRevenue(duration);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
    @GetMapping("/loyal")
    public ResponseEntity<?> getLoyalCustomers(@RequestParam(defaultValue = "3",required = false) Integer limit ,
    @RequestParam(defaultValue = "orders",required = false) String sort){
        try {
           List<MostLoyalCustomerProjection> response=service.getLoyalCustomers(limit, sort);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
