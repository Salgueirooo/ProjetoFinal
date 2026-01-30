package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.BakeryRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private String logo_id;

    @Column(nullable = false)
    private String phone_number;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    public Bakery(BakeryRequestDTO data, String logo, String logo_id) {
        this.name = data.name();
        this.logo = logo;
        this.logo_id = logo_id;
        this.phone_number = data.phone_number();
        this.email = data.email();
        this.address = data.address();
    }

    public void updateBakery(BakeryRequestDTO data, String logo,  String logo_id) {
        if (logo != null) this.logo = logo;
        if (logo_id != null) this.logo_id = logo_id;
        if (data.phone_number() != null) this.phone_number = data.phone_number();
        if (data.email() != null) this.email = data.email();
    }
}
