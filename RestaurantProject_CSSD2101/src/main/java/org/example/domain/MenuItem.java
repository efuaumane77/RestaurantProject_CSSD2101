package org.example.domain;

import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

//possible types of meals
enum MenuCategory { ENTREE, DRINK, DESSERT, COMBO }
enum DietaryType { REGULAR, VEGETARIAN, VEGAN, GLUTEN_FREE }

abstract class MenuItem {
    protected final String id, name, description;
    protected final double price;
    protected final MenuCategory category;
    protected final DietaryType dietaryType;
    protected boolean available;

    protected MenuItem(String id, String name, String description, double price,
                       MenuCategory category, DietaryType dietaryType){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.available = true;
    }

    abstract double calculatePrice();
    abstract boolean requiresKitchenPrep();
    abstract List<String> getRequiredIngredients();

    public void setAvailable(boolean available){
        this.available = available;
    }

    @Override
    public String toString() {
        return "%s[%s: %s | $%.2f | %s]"
                .formatted(getClass().getSimpleName(), id, name, price,
                        available ? "Available" : "Unavailable");
    }
}

final class Entree extends MenuItem {
    private final List<String> ingredients;
    private final int prepTimeMinutes;

    Entree(String id, String name, String description, double price, DietaryType dietaryType,
           List<String> ingredients, int prepTimeMinutes){
        super(id, name, description, price, MenuCategory.ENTREE, dietaryType);

        this.ingredients = new ArrayList<>(ingredients);
        this.prepTimeMinutes = prepTimeMinutes;
    }
    @Override double calculatePrice(){ return price;}
    @Override boolean requiresKitchenPrep(){ return true;}
    @Override List<String> getRequiredIngredients(){ return List.copyOf(ingredients);}

    public int getPrepTimeMinutes(){ return prepTimeMinutes;}
}

final class Drink extends MenuItem {
    private final boolean isAlcoholic;

    Drink(String id, String name, String description, double price, boolean isAlcoholic){
        super(id, name, description, price, MenuCategory.DRINK, DietaryType.REGULAR);
        this.isAlcoholic = isAlcoholic;
    }

    @Override double calculatePrice(){ return price;}
    @Override boolean requiresKitchenPrep(){ return false;}
    @Override List<String> getRequiredIngredients(){ return List.of(name.toLowerCase());}

    public boolean requiresAgeVerification(){return isAlcoholic;}
}

final class Dessert extends MenuItem {
    private List<String> allergens;

    Dessert(String id, String name, String description, double price, DietaryType dietaryType,
            List<String> allergens){
        super(id, name, description, price, MenuCategory.DESSERT, dietaryType);

        this.allergens = new ArrayList<>(allergens);
    }

    @Override double calculatePrice(){ return price;}
    @Override boolean requiresKitchenPrep(){return true;}
    @Override List<String> getRequiredIngredients(){ return List.of(name.toLowerCase());}

    public List<String> getAllergens(){return List.copyOf(allergens);}
}

final class Combo extends MenuItem {
    private final List<MenuItem> items;
    private final double discountPercent;

    Combo(String id, String name, String description, List<MenuItem> items,
          double discountPercent){
        super(id, name, description, 0.0, MenuCategory.COMBO, DietaryType.REGULAR);

        this.items = new ArrayList<>(items);
        this.discountPercent = discountPercent;
    }

    @Override
    double calculatePrice(){
        double total = items.stream().mapToDouble(MenuItem::calculatePrice).sum();
        return total * (1.0 - discountPercent/100.0);
    }

    @Override
    boolean requiresKitchenPrep(){
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
}

