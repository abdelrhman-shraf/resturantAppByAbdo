package com.example.SpringDataJpa.resturantApp.Orders.Projections;

import java.math.BigDecimal;

public interface MostLoyalCustomerProjection {
    /*c.customerId, COUNT(o.customer.customerId) AS numOrders,SUM(o.totalAmount) AS totalAmount\r\n" + //
        " ,c.firstName,c.lastName,c.phone,c.email */
    Long getCustomerId();
    Long getNumOrders();
    BigDecimal getTotalAmount();
    String getFirstName();
    String getLastName();
    String getPhone();
    String getEmail();
}
