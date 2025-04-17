package com.example.community_cr.diet.controller.dto.response.api.food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FoodItem {
	private int no;
	private String fd_Code;
	private String upper_Fd_Grupp_Nm;
	private String fd_Grupp_Nm;
	private String fd_Nm;
	private double fd_Wgh;
	private double food_Cnt;
	private String image_Address;
}
