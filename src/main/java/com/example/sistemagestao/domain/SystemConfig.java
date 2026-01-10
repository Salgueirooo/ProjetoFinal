package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "system_config")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String configKey;

    private String description;

    @Column(nullable = false)
    private String configValue;

    public SystemConfig(String configKey, String description ,String configValue) {
        this.configKey = configKey;
        this.description = description;
        this.configValue = configValue;
    }
}
