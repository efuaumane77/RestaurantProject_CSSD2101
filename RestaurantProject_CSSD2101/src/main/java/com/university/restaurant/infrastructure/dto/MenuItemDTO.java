package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Base DTO for MenuItem responses.
 * Uses Jackson annotations for polymorphic JSON serialization.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EntreeDTO.class, name = "ENTREE"),
        @JsonSubTypes.Type(value = DrinkDTO.class, name = "DRINK"),
        @JsonSubTypes.Type(value = DessertDTO.class, name = "DESSERT"),
        @JsonSubTypes.Type(value = ComboDTO.class, name = "COMBO")
})
public abstract class MenuItemDTO {
    private String id;
    private String name;
    private String description;
    private Double price;
    private MenuCategory category;
    private DietaryType dietaryType;
    private Boolean available;

    // Constructors
    protected MenuItemDTO() {}

    protected MenuItemDTO(String id, String name, String description, Double price,
                          MenuCategory category, DietaryType dietaryType, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.available = available;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public MenuCategory getCategory() { return category; }
    public void setCategory(MenuCategory category) { this.category = category; }

    public DietaryType getDietaryType() { return dietaryType; }
    public void setDietaryType(DietaryType dietaryType) { this.dietaryType = dietaryType; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}

/**
 * DTO for Entree items.
 */
class EntreeDTO extends MenuItemDTO {
    private List<String> ingredients;
    private Integer prepTimeMinutes;

    public EntreeDTO() {}

    public EntreeDTO(String id, String name, String description, Double price,
                     DietaryType dietaryType, Boolean available,
                     List<String> ingredients, Integer prepTimeMinutes) {
        super(id, name, description, price, MenuCategory.ENTREE, dietaryType, available);
        this.ingredients = ingredients;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
}

/**
 * DTO for Drink items.
 */
class DrinkDTO extends MenuItemDTO {
    private Boolean isAlcoholic;

    public DrinkDTO() {}

    public DrinkDTO(String id, String name, String description, Double price,
                    Boolean available, Boolean isAlcoholic) {
        super(id, name, description, price, MenuCategory.DRINK, DietaryType.REGULAR, available);
        this.isAlcoholic = isAlcoholic;
    }

    public Boolean getIsAlcoholic() { return isAlcoholic; }
    public void setIsAlcoholic(Boolean isAlcoholic) { this.isAlcoholic = isAlcoholic; }
}

/**
 * DTO for Dessert items.
 */
class DessertDTO extends MenuItemDTO {
    private List<String> allergens;

    public DessertDTO() {}

    public DessertDTO(String id, String name, String description, Double price,
                      DietaryType dietaryType, Boolean available, List<String> allergens) {
        super(id, name, description, price, MenuCategory.DESSERT, dietaryType, available);
        this.allergens = allergens;
    }

    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }
}

/**
 * DTO for Combo items.
 */
class ComboDTO extends MenuItemDTO {
    private List<String> itemIds;
    private Double discountPercent;

    public ComboDTO() {}

    public ComboDTO(String id, String name, String description, Double price,
                    Boolean available, List<String> itemIds, Double discountPercent) {
        super(id, name, description, price, MenuCategory.COMBO, DietaryType.REGULAR, available);
        this.itemIds = itemIds;
        this.discountPercent = discountPercent;
    }

    public List<String> getItemIds() { return itemIds; }
    public void setItemIds(List<String> itemIds) { this.itemIds = itemIds; }

    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }
}
