package com.example.SpringDataJpa.resturantApp.Orders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderDetailsRowsDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.TopSellingItemDto;

public interface OrderRepo extends JpaRepository<Order,Integer>  {
    @Query("SELECT new com.example.SpringDataJpa.resturantApp.Orders.dto.OrderDetailsRowsDto(" +
       "o.orderId, o.orderDatetime, o.status, o.paymentMethod, o.totalAmount, " +
       "c.customerId, c.firstName, c.lastName, c.phone, " +
       "mi.itemId, mi.itemName, od.quantity, od.unitPrice, od.subTotal) " +
       "FROM Order o " +
       "LEFT JOIN o.customer c " +
       "LEFT JOIN OrderDetails od ON od.order.orderId=o.orderId  " +
       "LEFT JOIN od.menuItem mi  "+
       "WHERE o.orderId =:Id")
    public List<OrderDetailsRowsDto> getOrderDetails(@Param("Id") int orderId);
    @Query(
        "SELECT new com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto("+
        "o.orderId,o.orderDatetime,o.status,o.paymentMethod,o.totalAmount ) "+
        " FROM Order o"
    )
    public Page<OrderResponseDto> getAllbyPage(Pageable pageale);
    @Query("SELECT new com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto("+
    "o.orderId,o.orderDatetime,o.status,o.paymentMethod,o.totalAmount)"+
    "FROM Order o WHERE o.customer.customerId=:id AND o.status='DELIVERED' ")
    public Page<OrderResponseDto> getCustomerOrderHistory(@Param("id") int customerId,Pageable pageale);
    @Query( value = "SELECT new com.example.SpringDataJpa.resturantApp.Orders.dto.TopSellingItemDto("
    +" mi.itemId,mi.itemName,COUNT(mi.itemId) AS timesOrdered  ,Sum(od.quantity) AS totalQuantitySold  , SUM(od.subTotal) AS totalRevenue)"
    +" FROM OrderDetails od LEFT JOIN od.menuItem mi "+
    " GROUP BY mi.itemId,mi.itemName")
    public Page<TopSellingItemDto> getTopSelling(Pageable pageable);
    @Query(value = "SELECT COALESCE( SUM(o.totalAmount),0) FROM Order o WHERE o.orderDatetime>=:targetDate AND o.status='DELIVERED' ")
    public BigDecimal getRevenue(@Param("targetDate") LocalDateTime date);

}

