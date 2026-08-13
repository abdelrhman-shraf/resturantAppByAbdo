package com.example.SpringDataJpa.resturantApp.Orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.SpringDataJpa.resturantApp.Customer.Customer;
import com.example.SpringDataJpa.resturantApp.MenuItems.MenuItem;
import com.example.SpringDataJpa.resturantApp.OrderDetails.OrderDetails;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.PaymentMethodEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    /* 
    CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_datetime DATETIME NOT NULL,
    status VARCHAR(30),
    payment_method VARCHAR(30),
    total_amount DECIMAL(10,2)
)*/
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "order_id",nullable = false )
private int orderId;
@CreationTimestamp
@Column(name = "order_datetime",updatable = false,nullable = false)
private LocalDateTime orderDatetime;
@Enumerated(EnumType.STRING)
@Column(name = "status",nullable = false,updatable = true)
private StatusEnum status;
@Enumerated(EnumType.STRING)
@Column(name = "payment_method")
private PaymentMethodEnum paymentMethod ;
@Column(name = "total_amount",precision = 10,scale = 2)
private BigDecimal totalAmount;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id",nullable = false)
private Customer customer;
@OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
private List<OrderDetails> orderDetails=new ArrayList<>();
public List<OrderDetails> getOrderDetails() {
    return this.orderDetails;
}
public void setOrderDetails(List<OrderDetails> orderDetails) {

    this.orderDetails = orderDetails;
}
public void addOrderDetail(OrderDetails orderDetails){
    this.orderDetails.add(orderDetails);
    orderDetails.setOrder(this);
}
public void addMenuItem(MenuItem menuItem,Integer quantity){
    OrderDetails detail = new OrderDetails();
    detail.setMenuItem(menuItem);
    detail.setQuantity(quantity);
    detail.setUnitPrice(menuItem.getPrice());
    detail.setSubTotal(detail.getUnitPrice(),quantity);
    detail.setOrder(this);
    this.orderDetails.add(detail);
    this.totalAmount=(this.totalAmount == null ? BigDecimal.ZERO : this.totalAmount).add(detail.getSubTotal());
    System.out.println("orderDetails subTotal value : " + detail.getSubTotal());
}
public Order(){}
public int getOrderId() {
    return orderId;
}

public LocalDateTime getOrderDatetime() {
    return orderDatetime;
}
public void setOrderDatetime(LocalDateTime orderDatetime) {
    this.orderDatetime = orderDatetime;
}
public StatusEnum getStatus() {
    return status;
}
public void setStatus(StatusEnum status) {
    this.status = status;
}
public PaymentMethodEnum getPaymentMethod() {
    return paymentMethod;
}
public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
    this.paymentMethod = paymentMethod;
}
public BigDecimal getTotalAmount() {
    return totalAmount;
}
public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
}
public Customer getCustomer() {
    return customer;
}
public void setCustomer(Customer customer) {
    this.customer = customer;
}





}
