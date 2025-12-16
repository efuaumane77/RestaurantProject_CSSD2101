package com.university.restaurant.model.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Efua Itoadon-Umane
 * 
 * This class represents the course of meal: DESSERT
 * This class is for creating new desser objects to add to the menu. OR for placing orders
 */

public final class Dessert extends MenuItem {
    private List<String> allergens;
    
    /**
     *
     * @param id
     * @param name
     * @param description
     * @param price
     * @param dietaryType
     * @param allergens tells us whether there are allergens in the desert (useful bcuz dessert
     *                  are prone to containing ingredients that ppl are allergic too)
     */
    public Dessert(String id, String name, String description, double price, DietaryType dietaryType,
                   List<String> allergens){
        super(id, name, description, price, MenuCategory.DESSERT, dietaryType);

        this.allergens = new ArrayList<>(allergens);
    }

    @Override
    public double calculatePrice(){ return price;}
    @Override
    public boolean requiresKitchenPrep(){return true;}
    @Override List<String> getRequiredIngredients(){ return List.of(name.toLowerCase());}

    public List<String> getAllergens(){return List.copyOf(allergens);}

    @Override
    public MenuItem copyWithPrice(double newPrice) {
        return new Dessert(
                this.id,
                this.name,
                this.description,
                newPrice,
                this.dietaryType,
                this.getAllergens()
        );
    }
}

