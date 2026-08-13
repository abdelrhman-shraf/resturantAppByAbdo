package com.example.SpringDataJpa.resturantApp.Categories;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringDataJpa.resturantApp.Customer.CustomerRequest;
import com.example.SpringDataJpa.resturantApp.Customer.CustomerResponse;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private CategoryService service;
    public CategoryController(CategoryService service){
        this.service=service;
    }
    
    @PostMapping("/create")
      public ResponseEntity<?> createCategory(@ RequestBody CategoryRequest request){
        try {
            CategoryResponse response=service.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("error happened while inserting new Category :( ");
        }
    }
    @PutMapping("/changename")
       public ResponseEntity<?> changeName(@RequestParam String name,@RequestParam int id){
        try {
            CategoryResponse response=service.updateName(name, id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("error happened while updating category name :( ");
        }
    }
    @DeleteMapping("/delete")
        public ResponseEntity<?> deleteCategory(@RequestParam int id){
        try {
            CategoryResponse response=service.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("error happened while deleting category  :( ");
        }
    }
    
    @GetMapping("/getall")    
       public ResponseEntity<?> getall(){
        try {
            
            
            return ResponseEntity.status(HttpStatus.OK).body(service.getCategoriesList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading categories  :( ");
        }
    }

}
