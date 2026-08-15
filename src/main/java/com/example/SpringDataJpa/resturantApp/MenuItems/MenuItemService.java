package com.example.SpringDataJpa.resturantApp.MenuItems;


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.SpringDataJpa.resturantApp.Categories.Category;
import com.example.SpringDataJpa.resturantApp.Categories.CategoryRepo;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemRequestDto;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemResponseDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MenuItemService {
   private MenuItemRepo repo;
    private CategoryRepo categoryRepo;
    private MenuItemMapper mapper;
    @Autowired
    public MenuItemService(MenuItemRepo repo, CategoryRepo categoryRepo,MenuItemMapper mapper){
        this.repo=repo;
        this.categoryRepo=categoryRepo;
        this.mapper=mapper;
    }
    public MenuItemResponseDto addMenuItem(MenuItemRequestDto request){
        MenuItem menuItem;
        Category category=null;
        if (request.categoryId()!=null) {
            category=categoryRepo.findById(request.categoryId())
            .orElseThrow(()-> new EntityNotFoundException("Category with id:  " + request.categoryId() + " not found ") );

        }
        menuItem=mapper.toEntity(request);

        menuItem.setCategory(category);
         MenuItem saved=repo.save(menuItem);
         return mapper.toResponseDto(saved);
    }
    public MenuItemResponseDto updateItem(MenuItemRequestDto request,int id){
        MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new EntityNotFoundException("menu item with :  " + id + " not found ") );
        mapper.updateEntityFromDto(request, menuItem);
       if (request.categoryId() != null) {
        boolean categoryChanged = menuItem.getCategory() == null 
                || !request.categoryId().equals(menuItem.getCategory().getCategoryId());

        if (categoryChanged) {
            Category category = categoryRepo.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id: " + request.categoryId() + " not found"));
            menuItem.setCategory(category);
        }
    }
        return mapper.toResponseDto(menuItem);

    }
    public void deleteItem(int id){
         MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new EntityNotFoundException("menu item with :  " + id + " not found ") );
        repo.delete(menuItem);
    }
    public MenuItemResponseDto getById(int id){
         MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new EntityNotFoundException("menu item with :  " + id + " not found ") );
        return mapper.toResponseDto(menuItem);
    }
    public Page <MenuItemResponseDto> getallMenuItems(int page , int size){

        Pageable pageable=PageRequest.of(page, size);
        Page<MenuItem> menuItemPage = repo.findAll(pageable);
        return menuItemPage.map(mapper::toResponseDto);
    }
    public MenuItemResponseDto assignCategory(int menuItemId,Integer categoryId){
        Category category=null;
        if (categoryId !=null) {
            category=categoryRepo.findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category with id: " + categoryId + " not found"));;
        }
         
        MenuItem menuItem = repo.findById(menuItemId).orElseThrow(()-> new EntityNotFoundException("menu item with :  " + menuItemId + " not found ") );
        menuItem.setCategory(category);
        return mapper.toResponseDto(menuItem);

    }
    public List<MenuItemResponseDto> searchMenu(Integer categoryId,Integer minPrice,Integer maxPrice,String itemName,String sortMethod){
        Specification<MenuItem> spec=Specification.where(MenuSearchSpecifications.hasCategory(categoryId))
        .and(MenuSearchSpecifications.hasPriceRange(minPrice, maxPrice))
        .and(MenuSearchSpecifications.hasName(itemName));
        Sort sort;
        if (sortMethod.isEmpty() || sortMethod.isBlank()) {
             sort = Sort.by(Direction.DESC, "price");
        }
        else{
             switch (sortMethod.toLowerCase()) {
            case "desc": sort = Sort.by(Direction.DESC, "price");
                break;
                case "asc":sort=Sort.by(Direction.ASC, "price");
                break;
            default:
                sort = Sort.by(Direction.DESC, "price");
                break;
        }
        }
        List <MenuItem> listforitems=repo.findAll(spec,sort);
        List <MenuItemResponseDto> responseDto=new ArrayList<>();
        for (MenuItem menuItem : listforitems) {
            responseDto.add(mapper.toResponseDto(menuItem));
        }
        return responseDto;
    }

}
