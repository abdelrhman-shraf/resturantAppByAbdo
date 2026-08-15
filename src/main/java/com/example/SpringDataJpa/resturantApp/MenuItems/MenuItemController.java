package com.example.SpringDataJpa.resturantApp.MenuItems;

import java.util.List;

import org.springframework.data.domain.Page;
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
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemRequestDto;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemResponseDto;

@RestController
@RequestMapping("/menuitem")
public class MenuItemController {
    MenuItemService service;
    public MenuItemController( MenuItemService service){
        this.service=service;
    }
    @PostMapping("/addmenuitem")
      public ResponseEntity<?> addMenuItem(@RequestBody MenuItemRequestDto requestDto){
        try {
            MenuItemResponseDto response=service.addMenuItem(requestDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error happened while adding menu item :( ");
        }
    }
    @PutMapping("/update")
      public ResponseEntity<?> updateMenuItem(@RequestBody MenuItemRequestDto requestDto,@RequestParam int id){
        try {
            MenuItemResponseDto response=service.updateItem(requestDto,id);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while updating menu item :( ");
        }
    }
    @DeleteMapping("/delete")
       public ResponseEntity<?> delete(@RequestParam int id){
        try {
            service.deleteItem(id);
            return ResponseEntity.status(HttpStatus.OK).body("menu Item with id: " + id +" deleted succesfuly (: ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error happened while deleting menu item :( ");
        }
    }
    @GetMapping("/getbyid")
      public ResponseEntity<?> getById(@RequestParam int id){
        try {
            MenuItemResponseDto response= service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading menu item :( ");
        }
    }
    @GetMapping("/getall")
     public ResponseEntity<?> getall(@RequestParam(defaultValue = "0",required = false) Integer page ,@RequestParam(defaultValue = "5",required = false) Integer size){
        try {
            Page <MenuItemResponseDto> response=service.getallMenuItems(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while loading menu item :( ");
        }
    }
    @PutMapping("/assigncategory")
    public ResponseEntity<?> assignCategory(@RequestParam int menuItemId,@RequestParam(required = false) Integer categoryId){
        try {
            MenuItemResponseDto response=service.assignCategory(menuItemId, categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while Assigning category to menu item :( ");
        }
    }
    @GetMapping("/search")
      public ResponseEntity<?> searchMenuFilter(@RequestParam(required = false) Integer categoryId,@RequestParam(required = false) Integer minPrice
       ,@RequestParam(required = false) Integer maxPrice,@RequestParam(required = false) String name,
        @RequestParam(required = false,defaultValue = "desc") String sortStrategy){
        try {
           List <MenuItemResponseDto> response=service.searchMenu(categoryId, minPrice, maxPrice, name, sortStrategy);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error happened while searching for menu item :( ");
        }
    }



}
