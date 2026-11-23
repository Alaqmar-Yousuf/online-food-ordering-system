package com.foodorder.backend.controller;

import com.foodorder.backend.entity.User;
import com.foodorder.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return userRepository.findByUsername(username).map(user -> {
            if (updatedDetails.getAddress() != null) user.setAddress(updatedDetails.getAddress());
            if (updatedDetails.getPhone() != null) user.setPhone(updatedDetails.getPhone());
            if (updatedDetails.getAvatarUrl() != null) user.setAvatarUrl(updatedDetails.getAvatarUrl());
            if (updatedDetails.getEmail() != null) user.setEmail(updatedDetails.getEmail());
            
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }
}
