package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.MenuItemEntity;
import com.university.restaurant.model.menu.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for MenuItemEntity.
 * Provides CRUD operations and custom queries for menu items.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface MenuJpaRepository extends JpaRepository<MenuItemEntity, String> {

    /**
     * Find menu items by category (ENTREE, DRINK, DESSERT, COMBO).
     */
    List<MenuItemEntity> findByCategory(MenuCategory category);

    /**
     * Find all available menu items.
     */
    List<MenuItemEntity> findByAvailableTrue();

    /**
     * Find menu items by name (case-insensitive, partial match).
     */
    @Query("SELECT m FROM MenuItemEntity m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MenuItemEntity> searchByName(String name);

    /**
     * Find menu items by category and availability.
     */
    List<MenuItemEntity> findByCategoryAndAvailable(MenuCategory category, Boolean available);

    /**
     * Count available menu items.
     */
    long countByAvailableTrue();
}
