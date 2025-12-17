package com.university.restaurant.infrastructure.adapter.mapper;

import com.university.restaurant.infrastructure.entity.*;
import com.university.restaurant.model.menu.*;
import org.springframework.stereotype.Component;
import com.university.restaurant.infrastructure.entity.EntreeEntity;
import com.university.restaurant.infrastructure.entity.DrinkEntity;
import com.university.restaurant.infrastructure.entity.DessertEntity;
import com.university.restaurant.infrastructure.entity.ComboEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between MenuItem domain models and MenuItemEntity JPA entities.
 * Handles polymorphic conversion for Entree, Drink, Dessert, and Combo.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class MenuItemMapper {

    /**
     * Convert domain MenuItem to JPA entity.
     */
    public MenuItemEntity toEntity(MenuItem domain) {
        if (domain instanceof Entree entree) {
            return new EntreeEntity(
                    entree.getId(),
                    entree.getName(),
                    entree.getDescription(),
                    entree.calculatePrice(),
                    entree.getDietaryType(),
                    String.join(",", entree.getRequiredIngredients()),
                    entree.getPrepTimeMinutes()
            );
        } else if (domain instanceof Drink drink) {
            return new DrinkEntity(
                    drink.getId(),
                    drink.getName(),
                    drink.getDescription(),
                    drink.calculatePrice(),
                    drink.requiresAgeVerification()
            );
        } else if (domain instanceof Dessert dessert) {
            return new DessertEntity(
                    dessert.getId(),
                    dessert.getName(),
                    dessert.getDescription(),
                    dessert.calculatePrice(),
                    dessert.getDietaryType(),
                    String.join(",", dessert.getAllergens())
            );
        } else if (domain instanceof Combo combo) {
            String itemIds = combo.getItems().stream()
                    .map(MenuItem::getId)
                    .collect(Collectors.joining(","));
            return new ComboEntity(
                    combo.getId(),
                    combo.getName(),
                    combo.getDescription(),
                    0.0, // Discount percent - would need to be extracted from Combo
                    itemIds
            );
        }
        throw new IllegalArgumentException("Unknown MenuItem type: " + domain.getClass());
    }

    /**
     * Convert JPA entity to domain MenuItem.
     */
    public MenuItem toDomain(MenuItemEntity entity) {
        if (entity instanceof EntreeEntity entree) {
            List<String> ingredients = entree.getIngredients() != null
                    ? Arrays.asList(entree.getIngredients().split(","))
                    : List.of();

            Entree domainEntree = new Entree(
                    entree.getId(),
                    entree.getName(),
                    entree.getDescription(),
                    entree.getPrice(),
                    entree.getDietaryType(),
                    ingredients,
                    entree.getPrepTimeMinutes() != null ? entree.getPrepTimeMinutes() : 0
            );
            domainEntree.setAvailable(entity.getAvailable());
            return domainEntree;

        } else if (entity instanceof DrinkEntity drink) {
            Drink domainDrink = new Drink(
                    drink.getId(),
                    drink.getName(),
                    drink.getDescription(),
                    drink.getPrice(),
                    drink.getIsAlcoholic() != null ? drink.getIsAlcoholic() : false
            );
            domainDrink.setAvailable(entity.getAvailable());
            return domainDrink;

        } else if (entity instanceof DessertEntity dessert) {
            List<String> allergens = dessert.getAllergens() != null
                    ? Arrays.asList(dessert.getAllergens().split(","))
                    : List.of();

            Dessert domainDessert = new Dessert(
                    dessert.getId(),
                    dessert.getName(),
                    dessert.getDescription(),
                    dessert.getPrice(),
                    dessert.getDietaryType(),
                    allergens
            );
            domainDessert.setAvailable(entity.getAvailable());
            return domainDessert;

        } else if (entity instanceof ComboEntity combo) {
            // Note: Combo conversion is simplified here
            // In a real implementation, you'd need to fetch the component MenuItems
            throw new UnsupportedOperationException("Combo conversion not fully implemented");
        }

        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    /**
     * Convert list of entities to domain models.
     */
    public List<MenuItem> toDomainList(List<MenuItemEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
