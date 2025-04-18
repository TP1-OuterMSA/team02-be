package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
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
		if (dietRepository.existsByUserIdAndDateAndType(userId, dto.getDate(), dto.getMealType())) {
			throw new IllegalArgumentException("이미 해당 날짜의 " + dto.getMealType() + " 식단이 존재합니다.");
		}

		List<String> foodCodes = dto.getFoods().stream()
			.map(FoodRequest::getFoodCode)
			.toList();
		List<Food> foods = foodRepository.findAllByFoodCodeIn(foodCodes);
		if (foods.size() != dto.getFoods().size()) {
			Set<String> existingFoodCodes = foods.stream()
				.map(Food::getFoodCode)
				.collect(Collectors.toSet());

			List<String> notFoundFoodCodes = foodCodes.stream()
				.filter(foodCode -> !existingFoodCodes.contains(foodCode))
				.toList();

			throw new IllegalArgumentException("존재하지 않는 음식 코드 : " + String.join(", ", notFoundFoodCodes));
		}

		Diet diet = Diet.of(user, dto.getDate(), dto.getMealType());
		dietRepository.save(diet);

		Map<String, FoodRequest> foodRequestMap = dto.getFoods().stream()
			.collect(Collectors.toMap(FoodRequest::getFoodCode, Function.identity()));

		List<DietFood> dietFoods = foods.stream()
			.map(food -> {
				DietFood dietFood = null;
				FoodRequest foodRequest = foodRequestMap.get(food.getFoodCode());
				if (foodRequest != null) {
					dietFood = DietFood.from(
						foodRequest.getIntakeWeight(),
						foodRequest.getIntakeKcal(),
						diet, food);
				}
				return dietFood;
			})
			.toList();

		diet.addFoods(dietFoods);
		dietFoodRepository.saveAll(dietFoods);

		return Optional.of(DietResponse.from(diet, user.getRecommendKcal()));
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
