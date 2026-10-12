package com.example.foodwaste.dto;

import java.time.LocalDate;

public record FoodResponse(Long id, LocalDate date, String name, String category,
                           double prepared, double served, double donated,
                           double wasted, double wastePercent, String level) {
}
