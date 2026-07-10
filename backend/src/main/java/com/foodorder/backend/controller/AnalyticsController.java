package com.foodorder.backend.controller;

import com.foodorder.backend.payload.response.CustomerActivityDTO;
import com.foodorder.backend.payload.response.RestaurantRevenueDTO;
import com.foodorder.backend.payload.response.RiderPerformanceDTO;
import com.foodorder.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/admin/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantRevenueDTO>> getRestaurantRevenue() {
        return ResponseEntity.ok(orderRepository.findRestaurantRevenue());
    }

    @GetMapping("/admin/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerActivityDTO>> getCustomerActivity() {
        return ResponseEntity.ok(orderRepository.findCustomerActivity());
    }

    @GetMapping("/admin/riders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RiderPerformanceDTO>> getRiderPerformance() {
        return ResponseEntity.ok(orderRepository.findRiderPerformance());
    }
}
