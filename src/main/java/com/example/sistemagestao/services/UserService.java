package com.example.sistemagestao.services;


import com.example.sistemagestao.dto.UserResponseDTO;
import com.example.sistemagestao.repositories.UserRepository;
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
}
