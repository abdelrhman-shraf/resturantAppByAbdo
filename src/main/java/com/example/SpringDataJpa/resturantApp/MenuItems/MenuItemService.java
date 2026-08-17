package com.example.SpringDataJpa.resturantApp.MenuItems;


import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringDataJpa.resturantApp.Categories.Category;
import com.example.SpringDataJpa.resturantApp.Categories.CategoryRepo;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.BadRequestException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemRequestDto;
import com.example.SpringDataJpa.resturantApp.MenuItems.Dto.MenuItemResponseDto;



@Service
@Transactional(readOnly = true)
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
    @Transactional
    public MenuItemResponseDto addMenuItem(MenuItemRequestDto request){
        MenuItem menuItem;
        Category category=null;
        if (request.categoryId()!=null) {
            category=categoryRepo.findById(request.categoryId())
            .orElseThrow(()-> new ResourceNotFoundException("Category", "CategoryId", request.categoryId()) );
        }
        menuItem=mapper.toEntity(request);

        menuItem.setCategory(category);
         MenuItem saved=repo.save(menuItem);
         return mapper.toResponseDto(saved);
    }
    @Transactional
    public MenuItemResponseDto updateItem(MenuItemRequestDto request,int id){
        MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException("MenuItem", "MenuItemId", id) );
        mapper.updateEntityFromDto(request, menuItem);
       if (request.categoryId() != null) {
        boolean categoryChanged = menuItem.getCategory() == null 
            || !request.categoryId().equals(menuItem.getCategory().getCategoryId());

        if (categoryChanged) {
            Category category = categoryRepo.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", request.categoryId()));
            menuItem.setCategory(category);
        }
    }
        return mapper.toResponseDto(menuItem);

    }
    @Transactional
    public void deleteItem(int id){
         MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException("MenuItem", "MenuItemId", id));
        repo.delete(menuItem);
    }
    public MenuItemResponseDto getById(int id){
         MenuItem menuItem=repo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException("MenuItem", "MenuItemId", id) );
        return mapper.toResponseDto(menuItem);
    }
    public Page <MenuItemResponseDto> getallMenuItems(int page , int size){

        Pageable pageable=PageRequest.of(page, size);
        Page<MenuItem> menuItemPage = repo.findAll(pageable);
        return menuItemPage.map(mapper::toResponseDto);
    }
    @Transactional
    public MenuItemResponseDto assignCategory(int menuItemId,Integer categoryId){
        Category category=null;
        if (categoryId !=null) {
            category=categoryRepo.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        }
         
        MenuItem menuItem = repo.findById(menuItemId).orElseThrow(()-> new ResourceNotFoundException("MenuItem", "MenuItemId", menuItemId));
        menuItem.setCategory(category);
        return mapper.toResponseDto(menuItem);

    }
    public List<MenuItemResponseDto> searchMenu(Integer categoryId,BigDecimal minPrice,BigDecimal maxPrice,String itemName,String sortMethod){
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
        throw new BadRequestException("minPrice cannot be greater than maxPrice");
    }
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
