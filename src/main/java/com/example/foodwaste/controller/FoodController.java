package com.example.foodwaste.controller;

import com.example.foodwaste.dto.FoodRequest;
import com.example.foodwaste.dto.FoodResponse;
import com.example.foodwaste.dto.PredictionResponse;
import com.example.foodwaste.dto.SummaryResponse;
import com.example.foodwaste.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FoodController {

    private final FoodService service;
    private final DataSource dataSource;

    public FoodController(FoodService service, DataSource dataSource) {
        this.service = service;
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "foodwaste");
        try (Connection ignored = dataSource.getConnection()) {
            body.put("status", "UP");
            body.put("database", "UP");
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("status", "DOWN");
            body.put("database", "DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }

    @GetMapping("/food")
    public List<FoodResponse> list(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return service.list(q, category, start, end);
    }

    @GetMapping("/food/{id}")
    public FoodResponse get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping("/food")
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse create(@Valid @RequestBody FoodRequest request) {
        return service.create(request);
    }

    @PutMapping("/food/{id}")
    public FoodResponse update(@PathVariable("id") Long id, @Valid @RequestBody FoodRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/food/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return service.categories();
    }

    @GetMapping("/summary")
    public SummaryResponse summary(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return service.summary(start, end);
    }

    @GetMapping("/predict")
    public PredictionResponse predict(@RequestParam(name = "name") String name) {
        return service.predict(name);
    }
}
