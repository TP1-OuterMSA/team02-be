package com.example.community_cr.school_meal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.school_meal.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	List<Menu> findAllByNameIn(List<String> menuNames);
}
