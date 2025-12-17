package com.university.restaurant.infrastructure.config;

import com.university.restaurant.infrastructure.adapter.*;
import com.university.restaurant.infrastructure.adapter.mapper.*;
import com.university.restaurant.infrastructure.jpa.*;
import com.university.restaurant.port.*;
import com.university.restaurant.repository.*;
import com.university.restaurant.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for wiring the Hexagonal Architecture layers.
 *
 * This configuration:
 * - Creates JPA adapters (infrastructure) that implement repository ports
 * - Creates services (application layer) that use the ports
 * - Ensures Spring uses database repositories instead of in-memory ones
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Configuration
public class RestaurantConfig {

    // ========== ADAPTERS (Infrastructure → Application) ==========

    @Bean
    public MenuRepository menuRepository(MenuJpaRepository jpaRepo, MenuItemMapper mapper) {
        return new MenuJpaAdapter(jpaRepo, mapper);
    }

    @Bean
    public InventoryRepository inventoryRepository(InventoryJpaRepository jpaRepo, InventoryMapper mapper) {
        return new InventoryJpaAdapter(jpaRepo, mapper);
    }

    @Bean
    public OrderRepository orderRepository(OrderJpaRepository jpaRepo, OrderMapper mapper) {
        return new OrderJpaAdapter(jpaRepo, mapper);
    }

    @Bean
    public ReservationRepository reservationRepository(ReservationJpaRepository jpaRepo, ReservationMapper mapper) {
        return new ReservationJpaAdapter(jpaRepo, mapper);
    }

    @Bean
    public PaymentRepository paymentRepository(PaymentJpaRepository jpaRepo, PaymentMapper mapper) {
        return new PaymentJpaAdapter(jpaRepo, mapper);
    }

    @Bean
    public RestaurantAuditLogRepository auditLogRepository(AuditLogJpaRepository jpaRepo) {
        return new AuditLogJpaAdapter(jpaRepo);
    }

    // ========== SERVICES (Application Layer) ==========

    @Bean
    public MenuServicePort menuService(MenuRepository menuRepo, RestaurantAuditLogRepository audits) {
        return new MenuService(menuRepo, audits);
    }

    @Bean
    public InventoryServicePort inventoryService(InventoryRepository inventoryRepo,
                                                 MenuRepository menuRepo,
                                                 RestaurantAuditLogRepository audits) {
        return new InventoryService(inventoryRepo, menuRepo, audits);
    }

    @Bean
    public OrderServicePort orderService(OrderRepository orderRepo, RestaurantAuditLogRepository audits) {
        return new OrderService(orderRepo, audits);
    }

    @Bean
    public ReservationServicePort reservationService(ReservationRepository reservationRepo,
                                                     RestaurantAuditLogRepository audits) {
        return new ReservationService(reservationRepo, audits);
    }

    @Bean
    public PaymentServicePort paymentService(OrderRepository orders,
                                             PaymentRepository payments,
                                             RestaurantAuditLogRepository audits) {
        return new PaymentService(orders, payments, audits);
    }

    @Bean
    public AnalyticsServicePort analyticsService(OrderRepository orders) {
        return new AnalyticsService(orders);
    }
}