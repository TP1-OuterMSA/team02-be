package com.example.community_cr.diet.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.community_cr.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Diet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;

	@Enumerated(EnumType.STRING)
	private MealType type;

	@Builder.Default
	@OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DietFood> foods = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public static Diet of(User user, LocalDate date, MealType type) {
		return Diet.builder()
			.user(user)
			.date(date)
			.type(type)
			.build();
	}

	public void addFoods(List<DietFood> dietFoods) {
		foods.addAll(dietFoods);
	}
}
