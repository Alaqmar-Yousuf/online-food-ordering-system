import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import App from './App.jsx'
import './index.css'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import { CartProvider } from './context/CartContext.jsx'

// Import pages
import Dashboard from './pages/Dashboard.jsx'
import RestaurantMenu from './pages/RestaurantMenu.jsx'
import Checkout from './pages/Checkout.jsx'
import UserOrders from './pages/UserOrders.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import Profile from './pages/Profile.jsx'
import RestaurantDashboard from './pages/RestaurantDashboard.jsx'
import RiderDashboard from './pages/RiderDashboard.jsx'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/app" element={<App />}>
              <Route index element={<Navigate to="dashboard" replace />} />
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="restaurant/:id" element={<RestaurantMenu />} />
              <Route path="checkout" element={<Checkout />} />
              <Route path="orders" element={<UserOrders />} />
              <Route path="profile" element={<Profile />} />
              <Route path="admin" element={<AdminDashboard />} />
              <Route path="restaurant-dashboard" element={<RestaurantDashboard />} />
              <Route path="rider-dashboard" element={<RiderDashboard />} />
            </Route>
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)
