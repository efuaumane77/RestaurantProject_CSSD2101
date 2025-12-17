package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.port.AnalyticsServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Analytics operations.
 * Handles all analytics-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsServicePort analyticsService;

    public AnalyticsController(AnalyticsServicePort analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /analytics/top-selling?staffId=m1&staffName=Alice
     * Get top-selling menu items
     */
    @GetMapping("/top-selling")
    public ResponseEntity<?> getTopSellingItems(
            @RequestParam String staffId,
            @RequestParam String staffName) {
        try {
            Manager manager = new Manager(staffId, staffName);
            Map<String, Long> topSelling = analyticsService.topSellingItems(manager);
            return ResponseEntity.ok(topSelling);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /analytics/revenue/today?staffId=m1&staffName=Alice
     * Get total revenue for today
     */
    @GetMapping("/revenue/today")
    public ResponseEntity<?> getTodayRevenue(
            @RequestParam String staffId,
            @RequestParam String staffName) {
        try {
            Manager manager = new Manager(staffId, staffName);
            double revenue = analyticsService.totalRevenueToday(manager);
            return ResponseEntity.ok(Map.of("totalRevenueToday", revenue));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}