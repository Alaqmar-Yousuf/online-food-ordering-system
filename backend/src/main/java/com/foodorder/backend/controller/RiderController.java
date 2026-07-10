package com.foodorder.backend.controller;

import com.foodorder.backend.entity.Order;
import com.foodorder.backend.entity.User;
import com.foodorder.backend.enums.OrderStatus;
import com.foodorder.backend.repository.OrderRepository;
import com.foodorder.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/riders")
public class RiderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private User getAuthenticatedRider() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Rider not found"));
    }

    @GetMapping("/orders/available")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<?> getAvailableOrders() {
        List<Order> availableOrders = orderRepository.findByStatusAndRiderIsNull(OrderStatus.READY_FOR_PICKUP);
        return ResponseEntity.ok(availableOrders);
    }

    @GetMapping("/orders/my-deliveries")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<?> getMyDeliveries() {
        User rider = getAuthenticatedRider();
        List<Order> myDeliveries = orderRepository.findByRiderId(rider.getId());
        return ResponseEntity.ok(myDeliveries);
    }

    @PutMapping("/orders/{orderId}/accept")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<?> acceptOrder(@PathVariable Long orderId) {
        User rider = getAuthenticatedRider();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getRider() != null) {
            return ResponseEntity.badRequest().body("Order has already been accepted by another rider.");
        }

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            return ResponseEntity.badRequest().body("Order is not ready for pickup.");
        }

        order.setRider(rider);
        order.setStatus(OrderStatus.DELIVERING);
        Order updatedOrder = orderRepository.save(order);

        // Notify customer
        messagingTemplate.convertAndSend("/topic/orders/" + order.getUser().getId(), updatedOrder);
        // Notify restaurant
        messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), updatedOrder);
        // Notify all riders that this order is taken (so they can remove it from their available list)
        messagingTemplate.convertAndSend("/topic/riders/available", updatedOrder);

        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/orders/{orderId}/status")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        User rider = getAuthenticatedRider();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getRider() == null || !order.getRider().getId().equals(rider.getId())) {
            return ResponseEntity.badRequest().body("You are not assigned to this order.");
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        // Notify customer
        messagingTemplate.convertAndSend("/topic/orders/" + order.getUser().getId(), updatedOrder);
        // Notify restaurant
        messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), updatedOrder);

        return ResponseEntity.ok(updatedOrder);
    }
}
