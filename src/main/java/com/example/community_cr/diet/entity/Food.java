package com.example.community_cr.diet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String foodCode;

    @Column(nullable = false)
    private String foodName;

    //칼로리,탄단지 이외 추가 요소 필요
    private int kcalPer100g;
    private int proteinPer100g;
    private int fatPer100g;
    private int carbPer100g;


}
