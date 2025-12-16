package com.university.restaurant.model.menu;

import java.util.ArrayList;
import java.util.List;

public final class Combo extends MenuItem {
    private final List<MenuItem> items;
    private final double discountPercent;

    Combo(String id, String name, String description, List<MenuItem> items,
          double discountPercent){
        super(id, name, description, 0.0, MenuCategory.COMBO, DietaryType.REGULAR);

        this.items = new ArrayList<>(items);
        this.discountPercent = discountPercent;
    }

    @Override
    public double calculatePrice(){
        double total = items.stream().mapToDouble(MenuItem::calculatePrice).sum();
        return total * (1.0 - discountPercent/100.0);
    }

    @Override
    public boolean requiresKitchenPrep(){
        return items.stream().anyMatch(MenuItem::requiresKitchenPrep);
    }

    @Override
    List<String> getRequiredIngredients(){
        return items.stream().flatMap(items -> items.getRequiredIngredients().stream())
                .distinct().toList();
    }

    public List<MenuItem> getItems(){
        return List.copyOf(items);
    }

    @Override
    public MenuItem copyWithPrice(double newPrice) {
        // Combo price is computed from items & discount - throw exception.
        throw new UnsupportedOperationException("Cannot set price on Combo; it is computed.");
    }
}
