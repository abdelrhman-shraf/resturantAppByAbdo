package com.example.SpringDataJpa.resturantApp.Orders.Enums;

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
        if (value == null) return QUANTITY;
        return SortingTopSellerEnum.valueOf(value.toUpperCase());
    }
}
