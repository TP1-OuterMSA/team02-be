package com.example.community_cr.diet.controller.dto.response.api.Nutrition;

import com.example.community_cr.diet.entity.Food;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class NutritionItem {
	@XmlElement(name = "main_Food_Code")
	private String mainFoodCode;

	@XmlElement(name = "main_Food_Name")
	private String mainFoodName;

	@XmlElement(name = "idnt_List")
	private IdntList idntList;

	public Food toEntity() {
		return Food.builder()
			.protein(idntList.getProtein())
			.carb(idntList.getCarbohydrate())
			.fat(idntList.getFattyAcid())
			.foodCode(mainFoodCode)
			.foodName(mainFoodName)
			.build();
	}
}
