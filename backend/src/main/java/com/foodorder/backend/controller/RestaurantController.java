package com.foodorder.backend.controller;

import com.foodorder.backend.entity.Restaurant;
import com.foodorder.backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodorder.backend.entity.MenuItem;
import com.foodorder.backend.entity.User;
import com.foodorder.backend.payload.request.MenuItemRequest;
import com.foodorder.backend.payload.response.MessageResponse;
import com.foodorder.backend.repository.MenuItemRepository;
import com.foodorder.backend.repository.UserRepository;
import com.foodorder.backend.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    private Restaurant getAuthenticatedRestaurant() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user == null) return null;
        
        List<Restaurant> restaurants = restaurantRepository.findAll();
        for (Restaurant r : restaurants) {
            if (r.getOwner() != null && r.getOwner().getId().equals(user.getId())) {
                return r;
            }
        }
        return null;
    }

    @GetMapping("/my-restaurant")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> getMyRestaurant() {
        Restaurant restaurant = getAuthenticatedRestaurant();
        if (restaurant == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: No restaurant assigned to this user."));
        }
        return ResponseEntity.ok(restaurant);
    }

    @PostMapping("/menu")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> addMenuItem(@Valid @RequestBody MenuItemRequest request) {
        Restaurant restaurant = getAuthenticatedRestaurant();
        if (restaurant == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: No restaurant assigned to this user."));
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        menuItem.setRestaurant(restaurant);

        menuItemRepository.save(menuItem);
        return ResponseEntity.ok(new MessageResponse("Menu item added successfully!"));
    }

    @PutMapping("/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long itemId, @Valid @RequestBody MenuItemRequest request) {
        Restaurant restaurant = getAuthenticatedRestaurant();
        if (restaurant == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: No restaurant assigned to this user."));
        }

        Optional<MenuItem> itemOpt = menuItemRepository.findById(itemId);
        if (!itemOpt.isPresent() || !itemOpt.get().getRestaurant().getId().equals(restaurant.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Menu item not found or does not belong to your restaurant."));
        }

        MenuItem menuItem = itemOpt.get();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setCategory(request.getCategory());
        if (request.getIsAvailable() != null) {
            menuItem.setIsAvailable(request.getIsAvailable());
        }

        menuItemRepository.save(menuItem);
        return ResponseEntity.ok(new MessageResponse("Menu item updated successfully!"));
    }

    @DeleteMapping("/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long itemId) {
        Restaurant restaurant = getAuthenticatedRestaurant();
        if (restaurant == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: No restaurant assigned to this user."));
        }

        Optional<MenuItem> itemOpt = menuItemRepository.findById(itemId);
        if (!itemOpt.isPresent() || !itemOpt.get().getRestaurant().getId().equals(restaurant.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Menu item not found or does not belong to your restaurant."));
        }

        menuItemRepository.deleteById(itemId);
        return ResponseEntity.ok(new MessageResponse("Menu item deleted successfully!"));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            return ResponseEntity.ok(restaurant.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantRepository.save(restaurant));
    }
}
