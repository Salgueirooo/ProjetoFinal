package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Roles;
import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.UserResponseDTO;
import com.example.sistemagestao.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update-role/{id}")
    public void updateUserRole(@PathVariable Long id, @RequestBody Roles role){
        userService.updateUser(id, role);
    }

    @GetMapping("/get-username")
    public String getUsername(@AuthenticationPrincipal User user){
        return user.getName();
    }

}
