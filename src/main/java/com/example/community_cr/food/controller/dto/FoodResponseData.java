package com.example.community_cr.food.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FoodResponseData {
	private int result_Code;
	private String result_Msg;
	private int rcdcnt;
	private int page_No;
	private int total_Count;
	private List<FoodItem> list;
}
