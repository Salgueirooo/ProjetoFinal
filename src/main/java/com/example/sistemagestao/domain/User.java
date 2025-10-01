package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    @Column(nullable = false)
    private String phone_number;


    public User(String name, String email, String password, Roles role, String phone_number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone_number = phone_number;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == Roles.ADMIN) return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_CONFECTIONER"),
                new SimpleGrantedAuthority("ROLE_COUNTER_EMPLOYEE"),
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );
        else if (this.role == Roles.CONFECTIONER) return List.of(
                new SimpleGrantedAuthority("ROLE_CONFECTIONER"),
                new SimpleGrantedAuthority("ROLE_COUNTER_EMPLOYEE"),
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );
        else if (this.role == Roles.COUNTER_EMPLOYEE) return List.of(
                new SimpleGrantedAuthority("ROLE_COUNTER_EMPLOYEE"),
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );
        else return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
