package com.example.community_cr.diet.controller.dto.response.api.Nutrition;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class IdntList {
	@XmlElement(name = "food_Weight")
	private double foodWeight;

	@XmlElement(name = "energy_Qy") // 칼로리
	private double energyQy;

	@XmlElement(name = "prot_Qy") // 단백질
	private double protein;

	@XmlElement(name = "carbohydrate_Qy") // 탄수화물
	private double carbohydrate;

	@XmlElement(name = "sugar_Qy") // 당류
	private double sugar;

	@XmlElement(name = "fafref_Qy") // 지방산
	private double fattyAcid;

	@XmlElement(name = "fasatf_Qy") // 포화지방산
	private double saturatedFat;

	@XmlElement(name = "na_Qy") // 나트륨
	private double sodium;

	@XmlElement(name = "chole_Qy") // 콜레스테롤
	private double cholesterol;

	@XmlElement(name = "fibtg_Qy") // 식이섬유
	private double dietaryFiber;

	@XmlElement(name = "water_Qy") // 수분
	private double water;
}
