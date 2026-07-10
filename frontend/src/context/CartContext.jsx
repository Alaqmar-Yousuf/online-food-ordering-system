import React, { createContext, useState, useEffect } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({ restaurantId: null, items: [] });

  // Add item to cart
  const addToCart = (menuItem, restaurantId) => {
    setCart((prevCart) => {
      // If adding from a different restaurant, clear the cart first
      let currentItems = prevCart.restaurantId === restaurantId ? [...prevCart.items] : [];
      
      const existingItemIndex = currentItems.findIndex(item => item.menuItem.id === menuItem.id);
      
      if (existingItemIndex >= 0) {
        currentItems[existingItemIndex].quantity += 1;
      } else {
        currentItems.push({ menuItem, quantity: 1 });
      }

      return { restaurantId, items: currentItems };
    });
  };

  // Remove item from cart completely
  const removeFromCart = (menuItemId) => {
    setCart((prevCart) => {
      const currentItems = prevCart.items.filter(item => item.menuItem.id !== menuItemId);
      return { 
        restaurantId: currentItems.length === 0 ? null : prevCart.restaurantId, 
        items: currentItems 
      };
    });
  };

  // Decrease quantity
  const decreaseQuantity = (menuItemId) => {
     setCart((prevCart) => {
      let currentItems = [...prevCart.items];
      const existingItemIndex = currentItems.findIndex(item => item.menuItem.id === menuItemId);
      
      if (existingItemIndex >= 0) {
        if (currentItems[existingItemIndex].quantity > 1) {
          currentItems[existingItemIndex].quantity -= 1;
        } else {
          currentItems = currentItems.filter(item => item.menuItem.id !== menuItemId);
        }
      }

      return { 
        restaurantId: currentItems.length === 0 ? null : prevCart.restaurantId, 
        items: currentItems 
      };
    });
  };

  const clearCart = () => {
    setCart({ restaurantId: null, items: [] });
  };

  const cartTotal = cart.items.reduce((total, item) => total + (item.menuItem.price * item.quantity), 0);
  const cartCount = cart.items.reduce((count, item) => count + item.quantity, 0);

  return (
    <CartContext.Provider value={{ cart, addToCart, removeFromCart, decreaseQuantity, clearCart, cartTotal, cartCount }}>
      {children}
    </CartContext.Provider>
  );
};
