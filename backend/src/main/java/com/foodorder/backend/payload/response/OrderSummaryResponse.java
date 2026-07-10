package com.foodorder.backend.payload.response;

import com.foodorder.backend.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Flat DTO for order summaries returned to clients - avoids lazy-loading issues.
 */
public class OrderSummaryResponse {
    private Long id;
    private String username;
    private String restaurantName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private int itemCount;

    public OrderSummaryResponse(Order o) {
        this.id = o.getId();
        this.username = o.getUser() != null ? o.getUser().getUsername() : "—";
        this.restaurantName = o.getRestaurant() != null ? o.getRestaurant().getName() : "—";
        this.totalAmount = o.getTotalAmount();
        this.status = o.getStatus() != null ? o.getStatus().name() : "—";
        this.orderDate = o.getOrderDate();
        this.itemCount = o.getItems() != null ? o.getItems().size() : 0;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRestaurantName() { return restaurantName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public int getItemCount() { return itemCount; }
}
