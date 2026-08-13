package com.example.SpringDataJpa.resturantApp.Categories;

public class CategoryRequest {
      private String categoryName;
    private String description;
    public CategoryRequest(){}
    public CategoryRequest(  String categoryName,
     String description){
        this.categoryName=categoryName;
        this.description=description;
     }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
