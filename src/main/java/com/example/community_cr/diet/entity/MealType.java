package com.example.community_cr.diet.entity;

public enum MealType {
	BREAKFAST,
	LUNCH,
	DINNER,
	SNACK;

	public static MealType from(String value) {
		return MealType.valueOf(value.toUpperCase());
	}
}
