package com.foodorder.backend.controller;

import com.foodorder.backend.entity.MenuItem;
import com.foodorder.backend.entity.Restaurant;
import com.foodorder.backend.repository.MenuItemRepository;
import com.foodorder.backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu")
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenuForRestaurant(@PathVariable Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(menuItemRepository.findByRestaurantId(restaurantId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItem menuItem) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        menuItem.setRestaurant(restaurant.get());
        return ResponseEntity.ok(menuItemRepository.save(menuItem));
    }
}
