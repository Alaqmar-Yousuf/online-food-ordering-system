package com.foodorder.backend.payload.response;

import java.math.BigDecimal;

public class CustomerActivityDTO {
    private String customerName;
    private Long totalOrders;
    private BigDecimal totalSpent;

    public CustomerActivityDTO(String customerName, Long totalOrders, BigDecimal totalSpent) {
        this.customerName = customerName;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
}
