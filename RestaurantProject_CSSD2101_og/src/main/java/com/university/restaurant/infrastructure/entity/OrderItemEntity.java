package com.university.restaurant.infrastructure.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for OrderItem (line items in an order).
 * Represents a single menu item within an order.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many order items belong to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // Reference to menu item
    @Column(name = "menu_item_id", nullable = false, length = 50)
    private String menuItemId;

    @Column(name = "menu_item_name", nullable = false, length = 100)
    private String menuItemName;

    @Column(name = "price_at_order", nullable = false)
    private Double priceAtOrder;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    // Default constructor for JPA
    public OrderItemEntity() {}

    // Constructor
    public OrderItemEntity(String menuItemId, String menuItemName, Double priceAtOrder, Integer quantity) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public Double getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(Double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}