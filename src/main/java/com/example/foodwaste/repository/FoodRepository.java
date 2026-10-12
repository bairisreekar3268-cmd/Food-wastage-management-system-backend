package com.example.foodwaste.repository;

import com.example.foodwaste.entity.FoodRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<FoodRecord, Long> {
}
