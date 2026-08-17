package com.example.SpringDataJpa.resturantApp.Categories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringDataJpa.resturantApp.CustomExceptions.DuplicateResourceException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Transactional(readOnly = true)
@Service
public class CategoryService {
    private CategoryRepo repo;
    public CategoryService(CategoryRepo repo){
        this.repo=repo;
    }
    private record CategoryResponseDto(Integer id ,String name) {
        private static CategoryResponseDto fromCategory(Category category){
            return new CategoryResponseDto(category.getCategoryId(),category.getCategoryName());
        }
    }
    private CategoryResponse toResponse(Category category){
        return new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getDescription());
    }
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if (repo.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException("Category", "categoryName", request.getCategoryName());
        }
        Category category=new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        Category saved= repo.save(category);
        return toResponse(saved);
    }
    @Transactional
    public CategoryResponse updateName(String name , int id){
        
        //category.setCategoryName(name);
        repo.updateName(name, id);
        Category category = repo.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Category", "CategoryId", id));
        return toResponse(category);

    }
    @Transactional
    public CategoryResponse deleteCategory(int id){
         Category category = repo.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Category", "CategoryId", id));
        repo.deleteById(id);
        return toResponse(category);

    }
    public List<CategoryResponse> getCategoriesList(){
        List <CategoryResponse> list=new ArrayList<>();
        List<Category> cList=repo.findAll();
        for (Category category : cList) {
            list.add(toResponse(category));
        }
        return list;
    }
}
