package com.example.SpringDataJpa.resturantApp.Categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepo extends JpaRepository<Category,Integer> {
    @Modifying
    @Query(value = "UPDATE categories SET category_name=:name WHERE category_id=:id;",nativeQuery = true)
    public void updateName(@Param("name") String name,@Param("id") int id);
    public boolean existsByCategoryName(String CategoryName);

}
