package com.university.restaurant.model.menu;

import java.util.List;

public final class Drink extends MenuItem {
    private final boolean isAlcoholic;

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
