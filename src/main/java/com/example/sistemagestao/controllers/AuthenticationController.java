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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    public ResponseEntity<?> login(@RequestBody AuthenticationDTO data) {
        try {
            var tokenAuth = new UsernamePasswordAuthenticationToken(data.email(), data.password());

            var auth = authenticationManager.authenticate(tokenAuth);

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou password inválidos");
        }
    }

    @PostMapping("/register-client")
    public ResponseEntity registerClient(@RequestBody RegisterDTO data){
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
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
}
