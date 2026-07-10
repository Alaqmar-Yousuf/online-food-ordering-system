package com.foodorder.backend.payload.response;

public class RiderPerformanceDTO {
    private String riderName;
    private Long totalDeliveries;

    public RiderPerformanceDTO(String riderName, Long totalDeliveries) {
        this.riderName = riderName;
        this.totalDeliveries = totalDeliveries;
    }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }
    public Long getTotalDeliveries() { return totalDeliveries; }
    public void setTotalDeliveries(Long totalDeliveries) { this.totalDeliveries = totalDeliveries; }
}
