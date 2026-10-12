package com.example.foodwaste.dto;

import java.util.List;

public record SummaryResponse(Totals totals, List<Point> daily, List<Point> monthly,
                              List<FoodStat> foods, List<FoodStat> categories,
                              List<Suggestion> suggestions) {

    public record Totals(double prepared, double served, double donated, double wasted,
                         double wastePercent, String level, int records) { }

    public record Point(String label, double prepared, double wasted) { }

    public record FoodStat(String name, double prepared, double served, double donated,
                           double wasted, double wastePercent, String level) { }

    public record Suggestion(String level, String text) { }
}
