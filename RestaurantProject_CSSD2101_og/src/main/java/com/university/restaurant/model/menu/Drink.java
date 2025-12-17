package com.university.restaurant.model.menu;

import java.util.List;

/**
 * @author Efua Itoadon-Umane
 * 
 * represeents the course of meal: DRINK
 * This class is for creating new drink objects to add to the menu. OR for placing orders
 */

public final class Drink extends MenuItem {
    private final boolean isAlcoholic;

    /**
     *
     * @param id
     * @param name
     * @param description
     * @param price
     * @param isAlcoholic status of whether drink contains alcohol (useful if user needs to do id check)
     */
    public Drink(String id, String name, String description, double price, boolean isAlcoholic){
        super(id, name, description, price, MenuCategory.DRINK, DietaryType.REGULAR);
        this.isAlcoholic = isAlcoholic;
    }

    @Override
    public double calculatePrice(){ return price;}
    @Override
    public boolean requiresKitchenPrep(){ return false;}
    @Override
    List<String> getRequiredIngredients(){ return List.of(name.toLowerCase());}

    public boolean requiresAgeVerification(){return isAlcoholic;}

    @Override
    public MenuItem copyWithPrice(double newPrice) {
        return new Drink(
                this.id,
                this.name,
                this.description,
                newPrice,
                this.requiresAgeVerification()
        );
    }
}

