package com.example.community_cr.diet.controller.dto.response.api.Nutrition;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class NutritionApiResponse {
	@XmlElement(name = "body")
	private NutritionBody nutritionBody;

	@XmlAccessorType(XmlAccessType.FIELD)
	@Getter
	@NoArgsConstructor
	public static class NutritionBody {
		@XmlElement(name = "items")
		private NutritionItems nutritionItems;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@Getter
	@NoArgsConstructor
	public static class NutritionItems {
		@XmlElement(name = "item")
		private NutritionItem nutritionItem;
	}
}
