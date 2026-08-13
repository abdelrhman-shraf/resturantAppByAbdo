package com.example.SpringDataJpa.resturantApp.Orders.Enums;

public enum PaymentMethodEnum {
     CASH,
    CARD;
      public static PaymentMethodEnum fromString(String name){
      try {
          return PaymentMethodEnum.valueOf(name.toUpperCase());
      } catch (Exception e) {
        System.out.println("error happened while mapping String "+ name  + " to statusEnum for orders \n "+ e.getMessage());
        return null;
      }
      
    }
}
