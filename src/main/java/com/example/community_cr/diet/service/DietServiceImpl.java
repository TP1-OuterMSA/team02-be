package com.example.community_cr.diet.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
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

		List<Food> foods = foodRepository.findAllByFoodCodeIn(dto.getFoods());
		if (foods.size() != dto.getFoods().size()) {
			Set<String> existingFoodCodes = foods.stream()
				.map(Food::getFoodCode)
				.collect(Collectors.toSet());

			List<String> notFoundFoodCodes = dto.getFoods().stream()
				.filter(foodCode -> !existingFoodCodes.contains(foodCode))
				.toList();

			throw new IllegalArgumentException("존재하지 않는 음식 코드 : " + String.join(", ", notFoundFoodCodes));
		}

		Diet diet = Diet.of(user, dto.getDate(), dto.getMealType());
		dietRepository.save(diet);

		List<DietFood> dietFoods = foods.stream().map(food -> DietFood.from(
				100,
				food.getKcal() / 100 * 100, //100g 당 칼로리 / 100 * 무게 = 섭취 칼로리
				diet, food))
			.toList();

		diet.addFoods(dietFoods);
		dietFoodRepository.saveAll(dietFoods);

		return Optional.of(DietResponse.from(diet));
	}

	@Override
	public Optional<DietResponse> getDiet(long userId, long dietId) {
		Diet diet = dietRepository.findById(dietId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 ID입니다."));
		if (diet.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 식단만 조회할 수 있습니다.");
		}
		return Optional.of(DietResponse.from(diet));
	}

	@Override
	public List<DietResponse> getDiets(long userId, long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<Diet> dietList;
		if (cursor == 0) {
			dietList = dietRepository.findTopByUserIdOrderByDateDesc(userId, pageRequest);
		} else {
			dietList = dietRepository.findNextPagePosts(userId, cursor, pageRequest);
		}
		return dietList.stream()
			.map(DietResponse::from)
			.toList();
	}
}
