package com.example.sistemagestao.controllers;

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
    public ResponseEntity<CategoryResponseDTO> addCategory(@RequestBody CategoryRequestDTO data){
        return categoryService.add(data);
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
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO newData){
        return categoryService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public Long deleteCategoryById(@PathVariable Long id){
        return categoryService.deleteById(id);
    }
}
