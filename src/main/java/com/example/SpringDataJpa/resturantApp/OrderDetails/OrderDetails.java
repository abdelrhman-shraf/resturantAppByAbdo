package com.example.SpringDataJpa.resturantApp.OrderDetails;

import java.math.BigDecimal;

import com.example.SpringDataJpa.resturantApp.MenuItems.MenuItem;
import com.example.SpringDataJpa.resturantApp.Orders.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetails {
    /*
    CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    menu_item_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2)
) */
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "order_detail_id")
private int id;
@ManyToOne
@JoinColumn(name = "order_id")
private Order order;
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "menu_item_id")
private MenuItem menuItem;
@Column(name = "quantity",nullable = false)
private int quantity;
@Column(name = "unit_price",nullable = false)
private BigDecimal unitPrice;
@Column(name = "subtotal",insertable = false,updatable = false,nullable = true)
private BigDecimal subTotal;
public OrderDetails(){}
public int getId() {
    return id;
}

public Order getOrder() {
    return order;
}
public void setOrder(Order order) {
    this.order = order;
}
public MenuItem getMenuItem() {
    return menuItem;
}
public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
}
public int getQuantity() {
    return quantity;
}
public void setQuantity(int quantity) {
    this.quantity = quantity;
}
public BigDecimal getUnitPrice() {
    return unitPrice;
}
public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
}
public BigDecimal getSubTotal() {
    return this.unitPrice.multiply(BigDecimal.valueOf(quantity));
}
public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
}
public void setSubTotal(BigDecimal subTotal,int quantity){
    this.subTotal=subTotal.multiply(BigDecimal.valueOf(quantity));
}

}
