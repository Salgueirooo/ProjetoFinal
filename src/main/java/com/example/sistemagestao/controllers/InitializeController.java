package com.example.sistemagestao.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/initialize")
public class InitializeController {
    @GetMapping
    public String initiallize(){
        return "Conectado ao Servidor.";
    }
}
