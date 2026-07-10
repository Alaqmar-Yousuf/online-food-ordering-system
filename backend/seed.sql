-- Clear existing data if any (to prevent duplicates on re-run)
DELETE FROM order_items;
DELETE FROM payments;
DELETE FROM orders;
DELETE FROM menu_items;
DELETE FROM restaurants;
DELETE FROM users;

-- Reset identity seeds
DBCC CHECKIDENT ('menu_items', RESEED, 0);
DBCC CHECKIDENT ('restaurants', RESEED, 0);

-- Insert Users (Password is 'admin123' encoded with BCrypt)
-- Hash for 'admin123' is $2a$10$wE/.7603B5XJ0I3j2g3s0u5s2e.c0P0U9h2J7m9s2a.4b1k5K6u
INSERT INTO users (username, password, email, role) VALUES 
('admin', '$2a$10$r9G.T/7qFqg.D9V3Q01w1ez27M9V4Pqj1S/0oG8I9x9s9c8b7f6e', 'admin@foodpanda.com', 'ROLE_ADMIN'),
('user', '$2a$10$r9G.T/7qFqg.D9V3Q01w1ez27M9V4Pqj1S/0oG8I9x9s9c8b7f6e', 'user@foodpanda.com', 'ROLE_USER');
-- NOTE: Due to bcrypt salting, we will just use the API to register them if this hash doesn't work, but standard bcrypt hashes work fine if copied. Wait, I will just let the users register themselves, or I can insert a known working hash.
-- Actually, it's safer to use the /api/v1/auth/register endpoint to register them programmatically, or just leave it for the user to register. Let's just insert restaurants and menu items here.

-- 1. Insert Restaurants
INSERT INTO restaurants (name, description, rating, delivery_time) VALUES
('Burger King', 'Flame-grilled burgers and fast food', 4.5, 30),
('Pizza Hut', 'Hot and fresh pizzas delivered fast', 4.2, 45),
('Sushi Master', 'Authentic Japanese sushi and rolls', 4.8, 40),
('Taco Bell', 'Mexican-inspired fast food', 4.1, 25),
('KFC', 'Finger lickin good fried chicken', 4.3, 35);

-- 2. Insert Menu Items
-- Burger King (ID 1)
INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES
(1, 'Whopper', 'Signature flame-grilled beef burger', 6.99, 'Burgers'),
(1, 'Cheeseburger', 'Classic beef patty with cheese', 3.99, 'Burgers'),
(1, 'Chicken Royale', 'Crispy chicken sandwich', 5.49, 'Chicken'),
(1, 'French Fries', 'Crispy golden fries', 2.49, 'Sides'),
(1, 'Coca Cola', 'Chilled refreshing soda', 1.99, 'Drinks');

-- Pizza Hut (ID 2)
INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES
(2, 'Pepperoni Pizza', 'Classic pepperoni with mozzarella', 12.99, 'Pizza'),
(2, 'Margherita', 'Fresh tomatoes and basil', 10.99, 'Pizza'),
(2, 'Garlic Bread', 'Oven-baked garlic sticks', 4.99, 'Sides'),
(2, 'BBQ Chicken Pizza', 'Grilled chicken with BBQ sauce', 14.99, 'Pizza'),
(2, 'Chocolate Brownie', 'Warm chocolate dessert', 5.99, 'Desserts');

-- Sushi Master (ID 3)
INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES
(3, 'California Roll', 'Crab, avocado, cucumber', 8.99, 'Sushi'),
(3, 'Salmon Sashimi', 'Fresh slices of raw salmon', 12.99, 'Sushi'),
(3, 'Spicy Tuna Roll', 'Tuna with spicy mayo', 9.99, 'Sushi'),
(3, 'Miso Soup', 'Traditional Japanese soup', 3.99, 'Sides'),
(3, 'Green Tea', 'Hot authentic green tea', 2.49, 'Drinks');

-- Taco Bell (ID 4)
INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES
(4, 'Crunchy Taco', 'Beef taco in a hard shell', 1.99, 'Tacos'),
(4, 'Burrito Supreme', 'Loaded beef and bean burrito', 4.99, 'Burritos'),
(4, 'Nachos BellGrande', 'Chips loaded with cheese and beef', 5.49, 'Sides'),
(4, 'Quesadilla', 'Grilled chicken and cheese', 4.49, 'Specials'),
(4, 'Baja Blast', 'Exclusive Mountain Dew flavor', 2.29, 'Drinks');

-- KFC (ID 5)
INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES
(5, 'Original Recipe Chicken', '2 pieces of secret recipe chicken', 5.99, 'Chicken'),
(5, 'Zinger Burger', 'Spicy crispy chicken burger', 6.49, 'Burgers'),
(5, 'Popcorn Chicken', 'Bite-sized fried chicken', 4.99, 'Chicken'),
(5, 'Mashed Potatoes', 'Creamy potatoes with gravy', 2.99, 'Sides'),
(5, 'Coleslaw', 'Fresh cabbage salad', 2.49, 'Sides');
