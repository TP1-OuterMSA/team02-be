package com.example.community_cr.diet.controller.dto.response.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EvaluateInfo {
	private String evaluate;
	private List<NutritionInfo> recommendFoods;
}
