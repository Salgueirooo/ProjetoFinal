package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.IngredientRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "ingredient")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ingredient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurentUnits units;

    @Column(nullable = false)
    private Boolean allergenic;

    public Ingredient (IngredientRequestDTO data, MeasurentUnits units) {
        this.name = data.name();
        this.allergenic = data.allergenic() != null ? data.allergenic() : false;
        this.units = units;
    }

    public void updateIngredient(IngredientRequestDTO data, MeasurentUnits units) {
        if (data.name() != null) this.name = data.name();
        if (data.allergenic() != null) this.allergenic = data.allergenic();
        if (units != null) this.units = units;
    }
}
