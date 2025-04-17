package com.example.community_cr.diet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFood {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private double weightInGrams;

	private double kcal;

	@ManyToOne
	@JoinColumn(name = "diet_id")
	private Diet diet;

	@ManyToOne
	@JoinColumn(name = "food_id")
	private Food food;

	public static DietFood from(double weightInGrams, double kcal, Diet diet, Food food) {
		return DietFood.builder()
			.weightInGrams(weightInGrams)
			.kcal(kcal)
			.diet(diet)
			.food(food)
			.build();
	}
}
