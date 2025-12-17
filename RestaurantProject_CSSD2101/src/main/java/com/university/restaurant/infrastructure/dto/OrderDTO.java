package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.model.order.OrderStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Order responses.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class OrderDTO {
    private UUID id;
    private Integer tableNumber;
    private String assignedWaiterId;
    private OrderStatus status;
    private OffsetDateTime createdAt;
    private Double totalAmount;
    private List<OrderItemDTO> items;

    // Constructors
    public OrderDTO() {}

    public OrderDTO(UUID id, Integer tableNumber, String assignedWaiterId,
                    OrderStatus status, OffsetDateTime createdAt, Double totalAmount,
                    List<OrderItemDTO> items) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.assignedWaiterId = assignedWaiterId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public String getAssignedWaiterId() { return assignedWaiterId; }
    public void setAssignedWaiterId(String assignedWaiterId) { this.assignedWaiterId = assignedWaiterId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}

