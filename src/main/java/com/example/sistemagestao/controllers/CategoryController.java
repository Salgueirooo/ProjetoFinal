package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.CategoryResponseDTO;
import com.example.sistemagestao.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public void addCategory(@RequestBody CategoryRequestDTO data){
        categoryService.addCategory(data);
    }

    @GetMapping("/all")
    public List<CategoryResponseDTO> getAll(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id){
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO newData){
        return categoryService.updateCategory(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategoryById(@PathVariable Long id){
        categoryService.deleteById(id);
    }
}
