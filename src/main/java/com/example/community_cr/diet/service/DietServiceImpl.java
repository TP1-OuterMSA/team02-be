package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;
import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.DietFood;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.DietFoodRepository;
import com.example.community_cr.diet.repository.DietRepository;
import com.example.community_cr.diet.repository.FoodRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DietServiceImpl implements DietService {
	private final DietRepository dietRepository;
	private final DietFoodRepository dietFoodRepository;
	private final FoodRepository foodRepository;
	private final UserRepository userRepository;

	@Override
	public Optional<DietResponse> saveDiet(long userId, DietRequest dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 데이터가 존재하지 않습니다."));
		Optional<Diet> existDiet = dietRepository.findByUserIdAndDateAndType(userId, dto.getDate(), dto.getMealType());
		if (existDiet.isPresent()) {
			addDietFoodFromDietRequest(existDiet.get(), dto);
			return Optional.of(DietResponse.from(existDiet.get(), user.getRecommendKcal()));
		}

		Diet diet = Diet.of(user, dto.getDate(), dto.getMealType());
		dietRepository.save(diet);

		addDietFoodFromDietRequest(diet, dto);

		return Optional.of(DietResponse.from(diet, user.getRecommendKcal()));
	}

	private void addDietFoodFromDietRequest(Diet diet, DietRequest dto) {
		// 이미 diet에 포함된 foodName들을 추출
		Set<String> existingDietFoodCodes = diet.getFoods().stream()
			.map(dietFood -> dietFood.getFood().getFoodName())
			.collect(Collectors.toSet());

		// 새롭게 추가할 foodName 필터링
		List<String> foodNames = dto.getFoods().stream()
			.map(FoodRequest::getFoodName)
			.filter(foodCode -> !existingDietFoodCodes.contains(foodCode))
			.toList();

		if (foodNames.isEmpty()) {
			throw new IllegalArgumentException("새롭게 추가할 음식이 존재하지 않습니다.");
		}

		// List<Food> foods = foodRepository.findAllByFoodCodeIn(foodNames);
		List<Food> foods = foodRepository.findAllByFoodNameIn(foodNames);

		// 존재하지 않는 음식 코드 검증
		if (foods.size() != foodNames.size()) {
			Set<String> existingFoodCodes = foods.stream()
				.map(Food::getFoodName)
				.collect(Collectors.toSet());

			List<String> notFoundFoodCodes = foodNames.stream()
				.filter(foodCode -> !existingFoodCodes.contains(foodCode))
				.toList();

			throw new IllegalArgumentException("존재하지 않는 음식 코드 : " + String.join(", ", notFoundFoodCodes));
		}

		Map<String, FoodRequest> foodRequestMap = dto.getFoods().stream()
			.collect(Collectors.toMap(FoodRequest::getFoodName, Function.identity()));

		// DietFood 생성
		List<DietFood> dietFoodsToAdd = foods.stream()
			.map(food -> {
				FoodRequest foodRequest = foodRequestMap.get(food.getFoodName());
				if (foodRequest != null) {
					return DietFood.from(
						foodRequest.getIntakeWeight(),
						foodRequest.getIntakeKcal(),
						diet, food);
				}
				return null;
			})
			.filter(Objects::nonNull)
			.toList();

		diet.addFoods(dietFoodsToAdd);
		dietFoodRepository.saveAll(dietFoodsToAdd);
	}

	@Override
	public Optional<DietResponse> getDiet(long userId, long dietId) {
		Diet diet = dietRepository.findById(dietId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 ID입니다."));
		User user = diet.getUser();
		if (user.getId() != userId) {
			throw new IllegalArgumentException("자신의 식단만 조회할 수 있습니다.");
		}
		return Optional.of(DietResponse.from(diet, user.getRecommendKcal()));
	}

	@Override
	public List<DietResponse> getDiets(long userId, LocalDate date) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));

		List<Diet> dietList = dietRepository.findAllByDateAndUserId(date, userId);

		double recommendKcal = user.getRecommendKcal();
		return dietList.stream()
			.map(diet -> DietResponse.from(diet, recommendKcal))
			.toList();
	}

	@Override
	public List<LocalDate> getDietDates(long userId, YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1); // 2025-04-01
		LocalDate endDate = yearMonth.atEndOfMonth(); // 2025-04-30

		return dietRepository.findDietDatesBetween(startDate, endDate);
	}
}
