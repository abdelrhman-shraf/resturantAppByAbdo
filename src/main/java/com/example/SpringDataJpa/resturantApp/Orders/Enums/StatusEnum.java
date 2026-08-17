package com.example.SpringDataJpa.resturantApp.Orders.Enums;
import java.util.Arrays;
public enum StatusEnum {
     PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED;

    public static StatusEnum fromString(String name){
     if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Order status value cannot be null, empty, or blank.");
    }
        boolean exists=false;
          for (StatusEnum value : values()) {
            if (name.equals(value.toString())) {
              exists=true;
              break;
            }
          }
          if (exists) {
            return StatusEnum.valueOf(name.toUpperCase());
          }else{
             throw new IllegalArgumentException("Invalid status value: '" + name + "'. Allowed values are: " + Arrays.toString(values()));
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
