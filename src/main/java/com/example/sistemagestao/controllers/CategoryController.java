package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.CategoryResponseDTO;
import com.example.sistemagestao.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public void addCategory(@RequestBody CategoryRequestDTO data){
        categoryService.add(data);
    }

    @GetMapping("/all")
    public List<CategoryResponseDTO> getAll(){
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id){
        return categoryService.getById(id);
    }

    @PutMapping("/update/{id}")
    public void updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO newData){
        categoryService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategoryById(@PathVariable Long id){
        categoryService.delete(id);
    }
}
