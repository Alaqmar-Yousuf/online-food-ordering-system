package com.foodorder.backend.config;

import com.foodorder.backend.entity.MenuItem;
import com.foodorder.backend.entity.Restaurant;
import com.foodorder.backend.repository.MenuItemRepository;
import com.foodorder.backend.repository.OrderRepository;
import com.foodorder.backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.PrintWriter;
import java.nio.file.Paths;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.foodorder.backend.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (restaurantRepository.count() > 0) {
            System.out.println("Data already exists. Skipping data seeder to permanently preserve orders and analytics.");
            return;
        }

        System.out.println("Database is empty. Seeding 30 highly unique restaurants with NO repetitions...");

        StringBuilder credentialsLog = new StringBuilder();
        credentialsLog.append("Restaurant Credentials:\n======================\n\n");

        if (!userRepository.existsByUsername("admin")) {
            com.foodorder.backend.entity.User adminUser = new com.foodorder.backend.entity.User(
                "admin", 
                passwordEncoder.encode("admin123"), 
                "admin@foodpanda.com", 
                com.foodorder.backend.enums.UserRole.ROLE_ADMIN
            );
            userRepository.save(adminUser);
            System.out.println("Created Default Admin User -> Username: admin | Password: admin123");
        }

        if (!userRepository.existsByUsername("customer")) {
            com.foodorder.backend.entity.User customerUser = new com.foodorder.backend.entity.User(
                "customer", 
                passwordEncoder.encode("customer123"), 
                "customer@foodpanda.com", 
                com.foodorder.backend.enums.UserRole.ROLE_USER
            );
            customerUser.setAddress("42 Innovation Blvd, Tech District");
            customerUser.setPhone("+1-555-0199");
            userRepository.save(customerUser);
            System.out.println("Created Default Customer User -> Username: customer | Password: customer123");
        }

        StringBuilder ridersLog = new StringBuilder();
        ridersLog.append("Rider Credentials:\n======================\n\n");
        for (int i = 1; i <= 15; i++) {
            String riderUsername = "rider_" + i;
            if (!userRepository.existsByUsername(riderUsername)) {
                com.foodorder.backend.entity.User rider = new com.foodorder.backend.entity.User(
                    riderUsername,
                    passwordEncoder.encode("password123"),
                    riderUsername + "@foodpanda.com",
                    com.foodorder.backend.enums.UserRole.ROLE_RIDER
                );
                userRepository.save(rider);
            }
            ridersLog.append(String.format("Rider ID: %d\nUsername: %s\nPassword: password123\n\n", i, riderUsername));
        }
        
        try (PrintWriter out = new PrintWriter(Paths.get("../riders.txt").toFile())) {
            out.println(ridersLog.toString());
            System.out.println("Saved rider credentials to riders.txt in the project root.");
        } catch (Exception e) {
            System.out.println("Failed to write riders.txt: " + e.getMessage());
        }



        String[] restaurantNames = {
            "The Golden Slice", "Burger Forge", "Sushi Master", "Green Bowl", "Taco Fiesta",
            "Mama's Kitchen", "Spice Route", "Crispy Chicken Hub", "Ocean Catch", "Pasta House",
            "Steak & Grill", "Vegan Paradise", "The Noodle Bar", "Urban Diner", "Sweet Delights",
            "Morning Brew", "BBQ Pit", "Dim Sum Delight", "The French Bistro", "Mediterranean Spot",
            "Waffle Wonders", "Curry Leaves", "Wrap & Roll", "The Catch", "Pancake House",
            "Bento Box", "Tapas Corner", "The Healthy Salad", "Fire Oven Pizza", "Street Bites"
        };
        
        String[] restaurantImages = {
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1537047902294-62a40c20a6ae?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1525610553991-2bede1a236e2?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1544148103-0773bf10d330?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1560053608-13721e0d69e8?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1554679665-f5537f187268?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1521017432531-fbd92d768814?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1517001402241-149b80894ac0?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1568031813264-d394c5d474b9?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1528605248644-14dd04022da1?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1560624052-449f5ddf0c31?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1565895405138-6c3a1555cb6a?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1578474846511-04ba529f0b88?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1582285141045-816766465451?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1585518419759-7fac2e1f26af?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1590846406792-0adc7f928f1f?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1593026362391-4927eb7b6f3a?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1596773223019-38379f82633c?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1601509653851-9e797d19da0e?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1605335028456-c7dbb0e8b23c?w=500&auto=format&fit=crop"
        };
        
        String[] restaurantAddresses = {
            "124 Main St, Springfield", "88 Oak Ave, Metropolis", "45 Maple Dr, Gotham", "77 Cedar Ln, Star City", "901 Elm St, Central City",
            "234 Pine Rd, Coast City", "567 Washington Blvd, Bludhaven", "890 Broadway, National City", "12 Market St, Keystone City", "345 Park Ave, Hub City",
            "678 Lakeview Dr, Fawcett City", "901 River Rd, Midway City", "234 Mountain View, Ivy Town", "567 Valley Rd, Smallville", "890 Forest Ln, Blue Valley",
            "12 Ocean Blvd, Opal City", "345 Sunset Dr, Gateway City", "678 Sunrise Ave, Happy Harbor", "901 High St, Dakota City", "234 Low St, Monument Point",
            "567 Center St, St. Roch", "890 Middle St, Portsmouth", "12 North St, Fairfield", "345 South St, Riverdale", "678 East St, Greendale",
            "901 West St, Midvale", "234 Front St, Chester's Mill", "567 Back St, Castle Rock", "890 Side St, Derry", "12 Corner St, Haven"
        };
        
        String[] resCategories = {
            "Pizza", "Burgers", "Sushi", "Healthy", "Mexican",
            "Comfort", "Indian", "Chicken", "Seafood", "Pasta",
            "Steak", "Vegan", "Noodles", "Diner", "Desserts",
            "Cafe", "BBQ", "Dim Sum", "French", "Mediterranean",
            "Breakfast", "Curry", "Wraps", "Seafood", "Pancakes",
            "Japanese", "Tapas", "Healthy", "Pizza", "Street Food"
        };

        // Real food images by category to make sure menus are rich
        String[] burgerImages = {
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1586816001966-79b736744398?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1594212683935-8025251a37c5?w=400&auto=format&fit=crop"
        };

        String[] pizzaImages = {
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&auto=format&fit=crop"
        };

        String[] sushiImages = {
            "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1553621042-f6e147245754?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1583623025817-d180a2221dce?w=400&auto=format&fit=crop"
        };

        String[] dessertImages = {
            "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1563805042-7684c8a9e9ce?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1514517521153-1be72277b32f?w=400&auto=format&fit=crop"
        };
        
        String[] healthyImages = {
            "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?w=400&auto=format&fit=crop"
        };

        String[] mexicanImages = {
            "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1599974579688-8dbdd335c77f?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1615870216519-2f9fa575fa5c?w=400&auto=format&fit=crop"
        };
        
        String[] generalFoodImages = {
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=400&auto=format&fit=crop"
        };

        String[] itemPrefixes = {"Classic", "Spicy", "Deluxe", "Special", "Supreme", "Double", "Grilled", "Crispy", "Signature", "Premium"};
        String[] itemBases = {"Specialty", "Combo", "Bowl", "Platter", "Meal", "Wrap", "Bites", "Feast", "Box", "Bucket"};
        String[] itemIngredients = {"with extra cheese", "served with fries", "with house sauce", "topped with fresh herbs", "with spicy mayo", "served with garlic bread", "with fresh veggies", "slow-cooked to perfection", "with organic ingredients", "sweet and sour"};

        Random random = new Random();

        // Ensure exactly 30 unique restaurants
        for (int i = 0; i < 30; i++) {
            Restaurant r = new Restaurant();
            String resCategory = resCategories[i];
            r.setName(restaurantNames[i]);
            r.setDescription("Authentic and delicious " + resCategory + " food made fresh daily at " + restaurantNames[i] + ".");
            r.setImageUrl(restaurantImages[i]);
            r.setAddress(restaurantAddresses[i]);
            
            double rating = 3.5 + (1.5 * random.nextDouble());
            r.setRating(Math.round(rating * 10.0) / 10.0);
            r.setDeliveryTime(15 + random.nextInt(46));
            
            String rawUsername = "owner_" + restaurantNames[i].toLowerCase().replaceAll("[^a-z0-9]", "");
            com.foodorder.backend.entity.User owner = userRepository.findByUsername(rawUsername).orElse(null);
            if (owner == null) {
                owner = new com.foodorder.backend.entity.User(
                    rawUsername,
                    passwordEncoder.encode("password123"),
                    rawUsername + "@foodpanda.com",
                    com.foodorder.backend.enums.UserRole.ROLE_RESTAURANT
                );
                owner = userRepository.save(owner);
            }
            r.setOwner(owner);
            credentialsLog.append(String.format("Restaurant: %s\nUsername: %s\nPassword: password123\n\n", restaurantNames[i], rawUsername));
            
            restaurantRepository.save(r);

            // Seed items matching the restaurant's category
            int numItems = 10 + random.nextInt(6);
            List<MenuItem> items = new ArrayList<>();
            for (int j = 1; j <= numItems; j++) {
                MenuItem item = new MenuItem();
                
                String itemCategory = resCategory;
                String base = itemCategory.equals("Desserts") ? "Cake" : (itemCategory.equals("Healthy") ? "Salad" : itemBases[random.nextInt(itemBases.length)]);
                item.setName(itemPrefixes[random.nextInt(itemPrefixes.length)] + " " + base);
                item.setDescription(itemCategory + " " + itemIngredients[random.nextInt(itemIngredients.length)] + ".");
                
                double price = 5.99 + (20.0 * random.nextDouble());
                item.setPrice(new BigDecimal(Math.round(price * 100.0) / 100.0));
                item.setCategory(itemCategory);
                
                // Use LoremFlickr to guarantee 100% unique images for every single item without any repetition.
                // We use a unique lock ID based on the restaurant index (i) and item index (j).
                String keyword = itemCategory.toLowerCase().replaceAll("[^a-z]", "");
                item.setImageUrl("https://loremflickr.com/400/400/" + keyword + ",food,meal?lock=" + (i * 100 + j));
                
                item.setRestaurant(r);
                items.add(item);
            }
            menuItemRepository.saveAll(items);
        }
        
        try (PrintWriter out = new PrintWriter(Paths.get("../credentials.txt").toFile())) {
            out.println(credentialsLog.toString());
            System.out.println("Saved credentials to credentials.txt in the project root.");
        } catch (Exception e) {
            System.out.println("Failed to write credentials file: " + e.getMessage());
        }
        
        System.out.println("Data Seeding Complete! Enjoy your totally unique restaurants.");
    }
}
