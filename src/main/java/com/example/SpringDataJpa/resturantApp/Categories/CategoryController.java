package com.example.SpringDataJpa.resturantApp.Categories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/category")
public class CategoryController {
    private CategoryService service;
    public CategoryController(CategoryService service){
        this.service=service;
    }
    public record RenameCategoryRequest(
        String name
    ) {
    }
    
    @PostMapping
      public ResponseEntity<?> createCategory(@RequestBody(required = true) CategoryRequest request){
        try {
            CategoryResponse response=service.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("error happened while inserting new Category :( ");
        }
    }
    @PatchMapping("/{id}")
       public ResponseEntity<?> changeName(@PathVariable(required = true) int id,
       @RequestBody(required = true) RenameCategoryRequest renameCategoryRequest){
        try {
            CategoryResponse response=service.updateName(renameCategoryRequest.name(), id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("error happened while updating category name :( ");
        }
    }
    @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteCategory(@PathVariable(required = true) int id){
        try {
            CategoryResponse response=service.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("error happened while deleting category  :( ");
        }
    }
    
    @GetMapping    
       public ResponseEntity<?> getall(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.getCategoriesList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while loading categories  :( ");
        }
    }

}
