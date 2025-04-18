package com.example.community_cr.user.service;

import java.util.Optional;

public interface UserService {

	void updateKcal(long userId, double kcal);

	Optional<Double> getRecommendKcal(long userId);
}
