package com.foodorder.backend.payload.response;

import java.math.BigDecimal;

public class RestaurantRevenueDTO {
    private String restaurantName;
    private Long totalOrders;
    private BigDecimal totalRevenue;

    public RestaurantRevenueDTO(String restaurantName, Long totalOrders, BigDecimal totalRevenue) {
        this.restaurantName = restaurantName;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
