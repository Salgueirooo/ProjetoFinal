package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.UserResponseDTO;
import com.example.sistemagestao.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers(){
        return userService.getAll();
    }
}
