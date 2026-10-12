package com.example.foodwaste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodWasteApplication {
    public static void main(String[] args) {
        String url = System.getenv("DB_URL");
        if (url != null && (url.startsWith("mysql://") || url.startsWith("mysqls://"))) {
            System.setProperty("DB_URL", "jdbc:" + url.replaceFirst("^mysqls://", "mysql://"));
        }
        SpringApplication.run(FoodWasteApplication.class, args);
    }
}
