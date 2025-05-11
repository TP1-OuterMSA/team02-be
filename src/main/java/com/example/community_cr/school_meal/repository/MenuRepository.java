package com.example.community_cr.school_meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.school_meal.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	Menu findByName(String menuName);
}
