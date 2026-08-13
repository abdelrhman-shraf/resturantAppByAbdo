package com.example.SpringDataJpa.resturantApp.MenuItems;

import java.math.BigDecimal;

import com.example.SpringDataJpa.resturantApp.Categories.Category;

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
@Table(name = "menu_items")
public class MenuItem {
    /*
    menu_items (
    menu_item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    category_id INT
); */
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "menu_item_id")
private int itemId;
@Column(name = "item_name",nullable = false)
private String itemName;
private String description;
@Column(precision = 10,scale = 2,nullable = false)
private BigDecimal price;
private boolean available;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id",nullable = true)
private Category category;
public MenuItem(){}
public int getItemId() {
    return itemId;
}

public String getItemName() {
    return itemName;
}
public void setItemName(String itemName) {
    this.itemName = itemName;
}
public String getDescription() {
    return description;
}
public void setDescription(String description) {
    this.description = description;
}
public BigDecimal getPrice() {
    return price;
}
public void setPrice(BigDecimal price) {
    this.price = price;
}
public boolean isAvailable() {
    return available;
}
public void setAvailable(boolean available) {
    this.available = available;
}
public Category getCategory() {
    return category;
}
public void setCategory(Category category) {
    this.category = category;
}


}
