# Food Ordering System - Web Frontend

Modern, responsive web application for the Food Ordering & Delivery Management System built with **React**, **Vite**, and **Tailwind CSS**.

## Features
- **Customer Portal**: Browse restaurant menus, add items to cart, place orders, and track order status in real time.
- **Restaurant Dashboard**: Real-time incoming order stream, status management (Pending -> Preparing -> Ready for Pickup).
- **Rider Portal**: View available deliveries, accept deliveries, and update delivery progress (Delivering -> Delivered).
- **Admin Dashboard**: System-wide revenue metrics, customer activity analysis, and rider performance statistics.
- **Real-Time Updates**: WebSocket (STOMP over SockJS) communication for instant status synchronization.

## Quick Start

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Development Server
```bash
npm run dev
```
The application will launch at `http://localhost:5173`.

## Environment & Proxy
By default, API requests point to `http://localhost:8080/api/v1` and WebSocket connects to `http://localhost:8080/ws`. Ensure the Spring Boot backend is running before using the frontend.
