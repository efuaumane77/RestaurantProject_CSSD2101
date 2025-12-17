package com.university.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main entry point for the Restaurant Management System.
 *
 * This application follows Hexagonal Architecture (Ports & Adapters):
 * - Domain Layer: Pure business logic (Efua's work)
 * - Application Layer: Use cases and ports (Alice's work)
 * - Infrastructure Layer: Spring Boot, JPA, REST (Mahdis's work)
 *
 * @author Mahdis (Infrastructure Layer)
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.university.restaurant.infrastructure.jpa")
@EnableTransactionManagement
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);

        System.out.println("""
            
            ═══════════════════════════════════════════════════════════
            🍽️  RESTAURANT MANAGEMENT SYSTEM STARTED SUCCESSFULLY 🍽️
            ═══════════════════════════════════════════════════════════
            
            🌐 API Base URL: http://localhost:8080/api
            
            📚 Available Endpoints:
               - Menu:         /api/menu
               - Orders:       /api/orders
               - Reservations: /api/reservations
               - Inventory:    /api/inventory
               - Payments:     /api/payments
               - Analytics:    /api/analytics
               - Staff:        /api/staff
            
            📊 Database: PostgreSQL (restaurant_db)
            
            ═══════════════════════════════════════════════════════════
            """);
    }
}