package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.domain.Roles;
import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.AuthenticationDTO;
import com.example.sistemagestao.dto.LoginResponseDTO;
import com.example.sistemagestao.dto.RegisterDTO;
import com.example.sistemagestao.infra.security.TokenService;
import com.example.sistemagestao.repositories.BakeryRepository;
import com.example.sistemagestao.repositories.UserRepository;
import com.example.sistemagestao.services.OrderService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private OrderService orderService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register-client")
    public ResponseEntity registerClient(@RequestBody RegisterDTO data){
        if(this.userRepository.findByEmail(data.email()) != null){
            throw new EntityExistsException("Já existe um utilizador com este email.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, Roles.CLIENT, data.phone_number());

        this.userRepository.save(newUser);

        List<Bakery> bakeryList = bakeryRepository.findAll();
        for(Bakery bakery : bakeryList){
            orderService.initialize(bakery.getId(), newUser);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data){
        if(this.userRepository.findByEmail(data.email()) != null){
            throw new EntityExistsException("Já existe um utilizador com este email.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, data.role(), data.phone_number());

        this.userRepository.save(newUser);

        List<Bakery> bakeryList = bakeryRepository.findAll();
        for(Bakery bakery : bakeryList){
            orderService.initialize(bakery.getId(), newUser);
        }

        return ResponseEntity.ok().build();
    }
}
