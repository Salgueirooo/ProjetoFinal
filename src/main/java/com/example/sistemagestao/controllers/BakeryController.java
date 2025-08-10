package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.BakeryRequestDTO;
import com.example.sistemagestao.repositories.BakeryRepository;
import com.example.sistemagestao.services.BakeryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/bakery")
public class BakeryController {

    @Autowired
    private BakeryService bakeryService;

    @PostMapping("/add")
    public void addBakery(@RequestBody BakeryRequestDTO data){
        bakeryService.add(data);
    }

    @PutMapping("/update/{id}")
    public void updateBakery(@PathVariable Long id, @RequestBody BakeryRequestDTO newData){
        bakeryService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBakeryById(@PathVariable Long id){
        bakeryService.delete(id);
    }
}
