package com.example.sistemagestao.services;


import com.example.sistemagestao.domain.Roles;
import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.UserResponseDTO;
import com.example.sistemagestao.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDTO> getAll(){
        return userRepository.findAllByOrderByNameAsc()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @Transactional
    public void updateUser(Long id, Roles role){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilizador não encontrado."));
        user.setRole(role);

        userRepository.save(user);
    }
}
