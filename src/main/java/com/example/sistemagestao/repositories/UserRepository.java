package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByNameAsc();
    UserDetails findByEmail(String email);
}
