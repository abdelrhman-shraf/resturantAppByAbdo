package com.example.SpringDataJpa.resturantApp.Orders;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;
import com.example.SpringDataJpa.resturantApp.Customer.Customer;
import com.example.SpringDataJpa.resturantApp.Customer.CustomerRepo;
import com.example.SpringDataJpa.resturantApp.MenuItems.MenuItem;
import com.example.SpringDataJpa.resturantApp.MenuItems.MenuItemRepo;
import com.example.SpringDataJpa.resturantApp.OrderDetails.OrderDetails;
import com.example.SpringDataJpa.resturantApp.OrderDetails.OrderDetailsRepo;
import com.example.SpringDataJpa.resturantApp.OrderDetails.dto.OrderDetailsRequestDto;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.SortingTopSellerEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Projections.MostLoyalCustomerProjection;
import com.example.SpringDataJpa.resturantApp.Orders.dto.CreateOrderDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.CustomerForOrdersDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.ItemForOrderDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderDetailsRowsDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderInfoResponse;
import com.example.SpringDataJpa.resturantApp.Orders.dto.OrderResponseDto;
import com.example.SpringDataJpa.resturantApp.Orders.dto.TopSellingItemDto;
import jakarta.persistence.EntityNotFoundException;

@Transactional(readOnly = true)
@Service
public class OrderService {
    
   private OrderRepo repo;
   private CustomerRepo customerRepo;
   private MenuItemRepo menuItemRepo;
   private OrderDetailsRepo orderDetailsRepo;
   @Autowired
   public OrderService(OrderRepo repo,CustomerRepo customerRepo,MenuItemRepo menuItemRepo,OrderDetailsRepo orderDetailsRepo){
    this.repo=repo;
    this.customerRepo=customerRepo;
    this.menuItemRepo=menuItemRepo;
    this.orderDetailsRepo=orderDetailsRepo;
   }
   private OrderResponseDto toOrderResponseDto(Order order){
      return new OrderResponseDto(order.getOrderId(), order.getOrderDatetime()
      , order.getStatus(), order.getPaymentMethod(), order.getTotalAmount());
   }
   @Transactional
   public OrderResponseDto createOrder(CreateOrderDto createOrderDto){
    Customer customer = customerRepo.findById(createOrderDto.customerId())
    .orElseThrow(() -> new ResourceNotFoundException("Customer",
     "CustomerId", createOrderDto.customerId()));
      // get each item id 
    List<Integer> menuItemIds =createOrderDto.orderedItems().stream()
    .map(OrderDetailsRequestDto::itemId).distinct().toList();
    // map for each menu item and it's id 
    Map<Integer,MenuItem> menuItemsMap=new HashMap<>();

    List<MenuItem> allmenuItems=menuItemRepo.findAllById(menuItemIds);
    for (MenuItem menuItem : allmenuItems) {
      menuItemsMap.put(menuItem.getItemId(), menuItem);
    }

    for (Integer id:menuItemIds) {
      if (!menuItemsMap.containsKey(id)) {
         throw new ResourceNotFoundException("Menu-Item",
          "menu-Item's id", id);
      }
    }
    Order order=new Order();
    order.setCustomer(customer);
    order.setPaymentMethod(createOrderDto.paymentMethod());
    order.setStatus(StatusEnum.PENDING);
    order.setTotalAmount(BigDecimal.ZERO);
    for ( OrderDetailsRequestDto requestDto:createOrderDto.orderedItems()) {
      MenuItem menuItem = menuItemsMap.get(requestDto.itemId());
      order.addMenuItem(menuItem, requestDto.quantity());
    }
    List<OrderDetails> orderDetails=order.getOrderDetails();
    for (OrderDetails orderDetails2 : orderDetails) {
      System.out.println("orderDetails subtotal before db insertion : " + orderDetails2.getSubTotal());
    }
    orderDetailsRepo.saveAll(orderDetails);

    Order saved = repo.save(order);

    return toOrderResponseDto(saved);

   }
   @Transactional
   public OrderResponseDto cancelOreder(Integer orderId ){
      Order order = repo.findById(orderId)
      .orElseThrow(()->  new ResourceNotFoundException("Order",
     "orderId", orderId));
      if (order.getStatus()==StatusEnum.DELIVERED || order.getStatus()==StatusEnum.READY) {
         throw new IllegalStateException("Cannot cancel an order that is already " + order.getStatus());
      }
      if (order.getStatus()==StatusEnum.CANCELLED) {
         throw new IllegalStateException("Order is already cancelled");
      }
      order.setStatus(StatusEnum.CANCELLED);
      return toOrderResponseDto(order);
   }
   @Transactional
   public OrderResponseDto changeStatus(Integer orderId ,StatusEnum newState){
      Order order = repo.findById(orderId)
      .orElseThrow(()->  new ResourceNotFoundException("Order",
     "orderId", orderId));
      if (! order.getStatus().canTransactionTo(newState)) {
        throw new IllegalStateException("can't change state from : " + order.getStatus().toString() +" to : "+ newState.toString() );

      }
       order.setStatus(newState);
      return toOrderResponseDto(order);
   }
   @Transactional
   public OrderInfoResponse getOrderById(int id){
      //load order 
      List<OrderDetailsRowsDto> rows = repo.getOrderDetails(id);
      if (rows.isEmpty()) {
         throw  new ResourceNotFoundException("Order",
     "orderId", id);
      }
      OrderDetailsRowsDto first=rows.get(0);
     

      // load customer 
       CustomerForOrdersDto customerResponse=null;
       if (first.customerId()!=null) {
         customerResponse=new CustomerForOrdersDto(
      first.customerId(), first.firstName(), first.lastName(), first.phone());
       }
       
      // load each item
      List<ItemForOrderDto> items=new ArrayList<>();
      
      for (OrderDetailsRowsDto row : rows) {
         if (row.menuItemId()!=null) {
         items.add(new ItemForOrderDto(
         row.menuItemId(), row.name(), row.quantity(), row.unitPrice(), row.subtotal()));
         }

      }

      // return the response
      OrderInfoResponse response=new OrderInfoResponse(
      first.orderId(), first.dateTime(), first.status(), first.paymentMethod(), first.totalAmount(), customerResponse, items);
      return response;
   }
   
   public Page<OrderResponseDto> getAllOrders(int page,int size){
      Pageable pageable=PageRequest.of(page, size);
      Page<OrderResponseDto> pages=repo.getAllbyPage(pageable);
      return pages;

   }
   
   public Page<OrderResponseDto> CustomerOrdersHistory(int customerId,int page,int size){
      if (! repo.existsById(customerId)) {
         throw new ResourceNotFoundException("customer", "customerId", customerId);
      }
      Pageable pageable =PageRequest.of(page, size,Sort.by("orderDatetime").descending());
      Page<OrderResponseDto> orders=repo.getCustomerOrderHistory(customerId, pageable);
      return orders;
   }
   
   public Page<TopSellingItemDto> topSelling(String orderBy){
     
      SortingTopSellerEnum sEnum=SortingTopSellerEnum.fromString(orderBy);
      Pageable pageable=PageRequest.of(0, 1,Sort.by(sEnum.getSortingColumn()).descending());
      return repo.getTopSelling(pageable);
   }
   public BigDecimal getRevenue(String targetDuration){
      /*day , week , month */
      LocalDateTime date;
      if (! (targetDuration.isBlank() || targetDuration.isEmpty())) {
          switch (targetDuration.toLowerCase()) {
         case "day":
            date=LocalDate.now().atStartOfDay();
            break;
            case "week": date =LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
            break;
            case "month": date = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            break;
         default:
            throw new IllegalArgumentException("INVALID DURATION : " + targetDuration + " .Expected 'day' or 'week' or 'month' .");
      };
      }else{
         date =LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
      }
     
      return repo.getRevenue(date);
   }
   public List<MostLoyalCustomerProjection> getLoyalCustomers(int limit,String sort){
      if (limit<=0 || limit>25) {
         limit=3;
      }
      if ( ! (sort.isEmpty() || sort.isBlank())) {
      switch (sort.toLowerCase()) {
      case "amount": sort="totalAmount";
         break;
      case "orders": sort="numOrders";
   break;
      default:
        throw new IllegalArgumentException("INVALID SORTING METHOD -> [" + sort +" ] ||  allowed methods are ( amount , orders)");
         
   }
      }else{
         sort="numOrders";
      }
      Pageable pageable =PageRequest.of(0, limit,Sort.Direction.DESC,sort);
      return repo.loyalCustomers(pageable);
   }
   
}
