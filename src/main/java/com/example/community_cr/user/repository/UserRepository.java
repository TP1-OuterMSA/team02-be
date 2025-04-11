package com.example.community_cr.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
