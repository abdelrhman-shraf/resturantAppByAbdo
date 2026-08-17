package com.example.SpringDataJpa.resturantApp.Orders.Enums;
import java.util.Arrays;


public enum SortingTopSellerEnum {
    QUANTITY,TIMESORDERD,REVENUE;
    public String getSortingColumn(){
        if (this.toString()=="QUANTITY") {
            return "totalQuantitySold";
        }
        else if (this.toString()=="TIMESORDERD") {
            return "timesOrdered";
        }
        else if (this.toString()=="REVENUE") {
            return "totalRevenue";
        }
        else {
            throw new IllegalArgumentException("ENUM FOR THAT STRING DOESN'T EXIST !");
        }
    }
    public static SortingTopSellerEnum fromString(String value){
      if (value == null || value.isBlank()) {
        return QUANTITY; 
    }
    
    return Arrays.stream(values())
            .filter(enumValue -> enumValue.name().equalsIgnoreCase(value.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                    "Invalid sorting option: '" + value + "'. Allowed values are: " + Arrays.toString(values())
            ));
    }
}
