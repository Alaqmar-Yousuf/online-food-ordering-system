package com.foodorder.backend.payload.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    private List<OrderItemRequest> items;

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
