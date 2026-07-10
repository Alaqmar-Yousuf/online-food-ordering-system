package com.foodorder.backend.controller;

import com.foodorder.backend.entity.*;
import com.foodorder.backend.enums.OrderStatus;
import com.foodorder.backend.enums.PaymentMethod;
import com.foodorder.backend.enums.PaymentStatus;
import com.foodorder.backend.payload.request.OrderItemRequest;
import com.foodorder.backend.payload.request.OrderRequest;
import com.foodorder.backend.payload.response.MessageResponse;
import com.foodorder.backend.repository.*;
import com.foodorder.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(orderRequest.getRestaurantId());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Restaurant not found."));
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurantOpt.get());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : orderRequest.getItems()) {
            Optional<MenuItem> menuItemOpt = menuItemRepository.findById(itemReq.getMenuItemId());
            if (!menuItemOpt.isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: MenuItem " + itemReq.getMenuItemId() + " not found."));
            }
            MenuItem menuItem = menuItemOpt.get();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPriceAtTimeOfOrder(menuItem.getPrice());
            orderItem.setOrder(order);

            order.getItems().add(orderItem);

            BigDecimal itemTotal = menuItem.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        // Simple Payment Mock
        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Hardcoded for demo
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setOrder(order);
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        order.setPayment(payment);

        orderRepository.save(order);

        // Broadcast the new order to the specific restaurant
        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantOpt.get().getId(), order);

        return ResponseEntity.ok(new MessageResponse("Order placed successfully! Order ID: " + order.getId()));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orders = orderRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.foodorder.backend.payload.response.OrderSummaryResponse>> getAllOrders() {
        List<com.foodorder.backend.payload.response.OrderSummaryResponse> summaries =
            orderRepository.findAll().stream()
                .map(com.foodorder.backend.payload.response.OrderSummaryResponse::new)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/restaurant")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> getRestaurantOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }
        
        List<Restaurant> restaurants = restaurantRepository.findAll();
        Restaurant myRestaurant = null;
        for (Restaurant r : restaurants) {
            if (r.getOwner() != null && r.getOwner().getId().equals(user.getId())) {
                myRestaurant = r;
                break;
            }
        }
        
        if (myRestaurant == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: No restaurant assigned to this user."));
        }
        
        List<Order> orders = orderRepository.findByRestaurantId(myRestaurant.getId());
        return ResponseEntity.ok(orders);
    }

    @Autowired
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @PutMapping("/admin/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Order not found."));
        }
        
        Order order = orderOpt.get();
        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            orderRepository.save(order);
            
            // Broadcast the update to the specific user via WebSockets
            messagingTemplate.convertAndSend("/topic/orders/" + order.getUser().getId(), order);
            
            return ResponseEntity.ok(new MessageResponse("Order status updated to " + status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid status."));
        }
    }

    @PutMapping("/{id}/restaurant-status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> updateRestaurantOrderStatus(@PathVariable Long id, @RequestParam String status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Order not found."));
        }
        
        Order order = orderOpt.get();
        if (order.getRestaurant().getOwner() == null || !order.getRestaurant().getOwner().getId().equals(userDetails.getId())) {
             return ResponseEntity.badRequest().body(new MessageResponse("Error: Unauthorized to update this order."));
        }

        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            orderRepository.save(order);
            
            // Broadcast the update to the specific user via WebSockets
            messagingTemplate.convertAndSend("/topic/orders/" + order.getUser().getId(), order);
            // Broadcast to restaurant as well
            messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), order);
            
            if (order.getStatus() == OrderStatus.READY_FOR_PICKUP) {
                messagingTemplate.convertAndSend("/topic/riders/available", order);
            }
            
            return ResponseEntity.ok(new MessageResponse("Order status updated to " + status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid status."));
        }
    }
}
