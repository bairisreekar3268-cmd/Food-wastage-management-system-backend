package com.example.foodwaste.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record FoodRequest(
        @NotNull(message = "Date is required.") LocalDate date,
        @NotBlank(message = "Food name is required.") @Size(max = 80, message = "Food name is too long (max 80).") String name,
        @Size(max = 40, message = "Category is too long (max 40).") String category,
        @NotNull(message = "Prepared quantity is required.") @Positive(message = "Prepared must be greater than 0.") Double prepared,
        @NotNull(message = "Served quantity is required.") @PositiveOrZero(message = "Served must be 0 or more.") Double served,
        @PositiveOrZero(message = "Donated must be 0 or more.") Double donated) {
}
