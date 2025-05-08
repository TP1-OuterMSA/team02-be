package com.example.community_cr.school_meal.entity;

import java.util.List;

import com.example.community_cr.diet.entity.MealType;
import com.example.kafka_schemas.MealEvent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "meal",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"day_info", "meal_type"})
	}
)
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String dayInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MealType mealType;

	@OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
	private List<MealMenu> mealMenus;

	public static Meal of(String dayInfo, MealType mealType) {
		return Meal.builder()
			.dayInfo(dayInfo)
			.mealType(mealType)
			.build();
	}

	public static Meal from(MealEvent mealEvent) {
		String dayInfo = mealEvent.getDate();
		MealType mealType = MealType.from(mealEvent.getMealType());
		return Meal.builder()
			.dayInfo(dayInfo)
			.mealType(mealType)
			.build();
	}
}
