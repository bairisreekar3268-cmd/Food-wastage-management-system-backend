package com.example.foodwaste.dto;

/** recommended is null when fewer than 3 past records exist for the food. */
public record PredictionResponse(Double recommended, int basedOnRecords) {
}
