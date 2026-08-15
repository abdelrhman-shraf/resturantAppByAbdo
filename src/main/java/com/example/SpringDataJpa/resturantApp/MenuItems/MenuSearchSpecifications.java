package com.example.SpringDataJpa.resturantApp.MenuItems;

import org.springframework.data.jpa.domain.Specification;

public class MenuSearchSpecifications {
    public static Specification<MenuItem> hasCategory(Integer category){
        return (root,query,builder)->{
            if (category==null) {
                return null;
            }
            return builder.equal(root.get("category").get("categoryId"), category);
        };
    }
    public static Specification<MenuItem> hasPriceRange(Integer minPrice , Integer maxPrice){

        return (root,query,builder)->{
            if (minPrice==null && maxPrice==null) {
                return null;
            }
            if (minPrice==null && maxPrice!=null) {
                 return builder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            if (minPrice!=null && maxPrice==null) {
                return builder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
           return builder.between(root.get("price"), minPrice, maxPrice);
        };
    }
    public static Specification<MenuItem> hasName(String name){
        return (root,query,builder)->{
            if (name==null || name.isBlank()) {
                return null;
            }
            return builder.like(builder.lower(root.get("itemName")), 
            "%" + name.trim().toLowerCase() + "%"
        );
        };
    }
    
}
