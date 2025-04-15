package com.example.community_cr.diet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodCode;

    private String foodName;

    private int weightInGrams;

    private int kcal;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;
}
