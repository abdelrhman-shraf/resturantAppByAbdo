package com.example.SpringDataJpa.resturantApp.MenuItems;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
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
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemRequestDto;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemResponseDto;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/menuitem")
public class MenuItemController {
    MenuItemService service;
    public MenuItemController( MenuItemService service){
        this.service=service;
    }
    public record AssignCategoryForMenuItem(
        Integer id
    ) {
    }
    public record MenuItemSearchCriteria(
    String name,
    Integer categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    String sortDirection  // "asc" or "desc"
) {
    // Compact constructor to set defaults safely
    public MenuItemSearchCriteria {
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
    }
}
    @PostMapping
      public ResponseEntity<?> addMenuItem(@RequestBody MenuItemRequestDto requestDto){
        try {
            MenuItemResponseDto response=service.addMenuItem(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while adding menu item :( ");
        }
    }
    @PutMapping("/{id}")
      public ResponseEntity<?> updateMenuItem(@PathVariable(required = true) int id,@RequestBody MenuItemRequestDto requestDto){
        try {
            MenuItemResponseDto response=service.updateItem(requestDto,id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while updating menu item :( ");
        }
    }
    @DeleteMapping("/{id}")
       public ResponseEntity<?> delete(@PathVariable(required = true) int id){
        try {
            service.deleteItem(id);
            return ResponseEntity.status(HttpStatus.OK).body("menu Item with id: " + id +" deleted succesfuly (: ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while deleting menu item :( ");
        }
    }
    @GetMapping("/{id}")
      public ResponseEntity<?> getById(@PathVariable(required = true) int id){
        try {
            MenuItemResponseDto response= service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading menu item :( ");
        }
    }
    @GetMapping
     public ResponseEntity<?> getall(@RequestParam(defaultValue = "0",required = false) Integer page ,@RequestParam(defaultValue = "10",required = false) Integer size){
        try {
            Page <MenuItemResponseDto> response=service.getallMenuItems(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading menu item :( ");
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> assignCategory(@PathVariable int id,
        @RequestBody AssignCategoryForMenuItem categoryId
    ){
        try {
            MenuItemResponseDto response=service.assignCategory(id, categoryId.id());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while Assigning category to menu item :( ");
        }
    }
    @GetMapping("/search")
      public ResponseEntity<?> searchMenuFilter(@RequestBody(required = true) MenuItemSearchCriteria criteria){
        try {
           List <MenuItemResponseDto> response=service.searchMenu(criteria.categoryId(), criteria.minPrice(), criteria.maxPrice()
           , criteria.name(), criteria.sortDirection());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while searching for menu item :( ");
        }
    }



}
