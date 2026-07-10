package com.foodorder.backend.repository;

import com.foodorder.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foodorder.backend.payload.response.RestaurantRevenueDTO;
import com.foodorder.backend.payload.response.CustomerActivityDTO;
import com.foodorder.backend.payload.response.RiderPerformanceDTO;

import java.util.List;

import com.foodorder.backend.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByStatusAndRiderIsNull(OrderStatus status);
    List<Order> findByRiderId(Long riderId);

    @Query("SELECT new com.foodorder.backend.payload.response.RestaurantRevenueDTO(r.name, COUNT(o), SUM(o.totalAmount)) " +
           "FROM Order o JOIN o.restaurant r GROUP BY r.name ORDER BY SUM(o.totalAmount) DESC")
    List<RestaurantRevenueDTO> findRestaurantRevenue();

    @Query("SELECT new com.foodorder.backend.payload.response.CustomerActivityDTO(u.username, COUNT(o), SUM(o.totalAmount)) " +
           "FROM Order o JOIN o.user u GROUP BY u.username ORDER BY SUM(o.totalAmount) DESC")
    List<CustomerActivityDTO> findCustomerActivity();

    @Query("SELECT new com.foodorder.backend.payload.response.RiderPerformanceDTO(rd.username, COUNT(o)) " +
           "FROM Order o JOIN o.rider rd WHERE o.status = 'DELIVERED' GROUP BY rd.username ORDER BY COUNT(o) DESC")
    List<RiderPerformanceDTO> findRiderPerformance();
}
