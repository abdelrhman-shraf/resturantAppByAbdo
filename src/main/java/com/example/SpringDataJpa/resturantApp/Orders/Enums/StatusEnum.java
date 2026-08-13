package com.example.SpringDataJpa.resturantApp.Orders.Enums;

public enum StatusEnum {
     PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED;

    public static StatusEnum fromString(String name){
      try {
          return StatusEnum.valueOf(name.toUpperCase());
      } catch (Exception e) {
        System.out.println("error happened while mapping String "+ name  + " to statusEnum for orders \n "+ e.getMessage());
        return null;
      }
      
    }
    public boolean canTransactionTo(StatusEnum statusEnum){
      boolean isallowed=false;
      switch (this) {
        case PENDING:isallowed= statusEnum.toString().equals("CANCELLED")|| statusEnum.toString().equals("CONFIRMED");
        break;
          case CONFIRMED:isallowed=statusEnum.toString().equals("CANCELLED")||statusEnum.toString().equals("PREPARING");
          break;
          case PREPARING: isallowed=statusEnum.toString().equals("CANCELLED") || statusEnum.toString().equals("READY");
          break;
          case READY :isallowed = statusEnum.toString().equals("DELIVERED");

          break;
      
        default:
          isallowed=false;
          break;
      } ;
      return isallowed;
      
    }

    
}
