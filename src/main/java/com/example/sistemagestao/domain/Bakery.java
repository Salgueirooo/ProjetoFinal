package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.BakeryRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "bakery")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bakery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String logo;

    @Column(nullable = false)
    private String phone_number;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    public Bakery(BakeryRequestDTO data) {
        this.name = data.name();
        this.logo = data.logo();
        this.phone_number = data.phone_number();
        this.email = data.email();
        this.address = data.address();
    }

    public void updateBakery(BakeryRequestDTO data) {
        if (data.name() != null) this.name = data.name();
        this.logo = data.logo();
        if (data.phone_number() != null) this.phone_number = data.phone_number();
        if (data.email() != null) this.email = data.email();
        if (data.address() != null) this.address = data.address();
    }
}
