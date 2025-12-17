package com.university.restaurant.infrastructure.dto;

/**
 * DTO for individual order items.
 */
public class OrderItemDTO {
    private String menuItemId;
    private String menuItemName;
    private Double priceAtOrder;
    private Integer quantity;

    public OrderItemDTO() {}

    public OrderItemDTO(String menuItemId, String menuItemName, Double priceAtOrder, Integer quantity) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
    }

    public String getMenuItemId() { return menuItemId; }
    public void setMenuItemId(String menuItemId) { this.menuItemId = menuItemId; }

    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

    public Double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}