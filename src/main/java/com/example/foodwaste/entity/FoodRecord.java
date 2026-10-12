package com.example.foodwaste.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/** One row of the original SQLite "food" table (id, date, name, prepared, served, donated) + category. */
@Entity
@Table(name = "food")
public class FoodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 40)
    private String category = "General";

    @Column(nullable = false)
    private double prepared;

    @Column(nullable = false)
    private double served;

    @Column(nullable = false)
    private double donated;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrepared() { return prepared; }
    public void setPrepared(double prepared) { this.prepared = prepared; }
    public double getServed() { return served; }
    public void setServed(double served) { this.served = served; }
    public double getDonated() { return donated; }
    public void setDonated(double donated) { this.donated = donated; }
}
