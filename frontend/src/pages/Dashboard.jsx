import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Star, Clock, Truck, Search, Filter } from 'lucide-react';
import api from '../services/api';

const Dashboard = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeCategory, setActiveCategory] = useState('All');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        const response = await api.get('/restaurants');
        setRestaurants(response.data);
      } catch (err) {
        console.error("Failed to fetch restaurants:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchRestaurants();
  }, []);

  if (loading) {
    return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;
  }

  // Extract unique categories (assuming category is derived from description or a dedicated field if added. We'll use mocked categories for the UI if missing)
  const categories = ['All', 'Pizza', 'Burgers', 'Sushi', 'Healthy', 'Mexican', 'Desserts', 'Chicken'];

  // Filter Logic
  const filteredRestaurants = restaurants.filter(restaurant => {
    const matchesSearch = restaurant.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          restaurant.description.toLowerCase().includes(searchQuery.toLowerCase());
    
    // Check if restaurant description contains the category (since our seeder put category in description)
    const matchesCategory = activeCategory === 'All' || restaurant.description.toLowerCase().includes(activeCategory.toLowerCase());
    
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="pb-24">
      {/* Header & Search */}
      <div className="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Discover Restaurants</h1>
          <p className="text-text-muted">Explore the best food near you.</p>
        </div>
        
        <div className="relative w-full md:w-96">
          <input 
            type="text" 
            placeholder="Search restaurants or cuisines..." 
            className="w-full bg-input-bg border border-glass-border rounded-full py-3 pl-12 pr-4 text-white focus:outline-none focus:border-primary transition-colors"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Search className="absolute left-4 top-3.5 text-text-muted" size={20} />
        </div>
      </div>

      {/* Categories Filter */}
      <div className="mb-8 overflow-x-auto pb-4 hide-scrollbar">
        <div className="flex gap-3">
          <div className="flex items-center gap-2 px-4 py-2 bg-glass-bg rounded-full border border-glass-border text-text-muted">
            <Filter size={16} /> Filters
          </div>
          <div className="w-px h-8 bg-glass-border mx-2 self-center"></div>
          {categories.map(category => (
            <button
              key={category}
              onClick={() => setActiveCategory(category)}
              className={`px-5 py-2 rounded-full whitespace-nowrap transition-all font-medium ${
                activeCategory === category 
                  ? 'bg-primary text-white shadow-lg shadow-primary/30' 
                  : 'bg-input-bg border border-glass-border text-text-muted hover:text-white hover:border-text-muted'
              }`}
            >
              {category}
            </button>
          ))}
        </div>
      </div>

      {/* Restaurant Grid */}
      {filteredRestaurants.length === 0 ? (
        <div className="text-center py-20 glass-panel rounded-3xl">
          <Search size={48} className="mx-auto text-text-muted mb-4" />
          <h2 className="text-xl font-bold text-white mb-2">No restaurants found</h2>
          <p className="text-text-muted">Try adjusting your search or filters to find what you're looking for.</p>
          <button onClick={() => {setSearchQuery(''); setActiveCategory('All');}} className="mt-4 text-primary hover:underline">Clear all filters</button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredRestaurants.map((restaurant, index) => (
            <div 
              key={restaurant.id} 
              onClick={() => navigate(`/app/restaurant/${restaurant.id}`)}
              className="glass-panel overflow-hidden cursor-pointer group hover:-translate-y-2 transition-all duration-300 animate-fade-in-up"
              style={{ animationDelay: `${(index % 6) * 100}ms` }}
            >
              <div className="h-48 overflow-hidden relative">
                <img 
                  src={restaurant.imageUrl || "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500&q=80"} 
                  alt={restaurant.name}
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                />
                <div className="absolute top-4 right-4 bg-glass-bg backdrop-blur-md px-3 py-1 rounded-full text-white text-sm font-semibold flex items-center gap-1 border border-glass-border shadow-lg">
                  <Star size={14} className="text-yellow-400 fill-yellow-400" />
                  {restaurant.rating ? restaurant.rating.toFixed(1) : '4.5'}
                </div>
              </div>
              
              <div className="p-5">
                <h3 className="text-xl font-bold text-white mb-1 group-hover:text-primary transition-colors">{restaurant.name}</h3>
                
                <div className="flex items-center text-text-muted text-sm mb-3 gap-1">
                  <MapPin size={14} />
                  <span className="truncate">{restaurant.address}</span>
                </div>
                
                <div className="flex items-center gap-4 text-xs font-medium">
                  <div className="flex items-center gap-1.5 text-text-light bg-input-bg px-2.5 py-1.5 rounded-md border border-glass-border">
                    <Clock size={14} className="text-primary" />
                    {restaurant.deliveryTime ? `${restaurant.deliveryTime} min` : '20-30 min'}
                  </div>
                  <div className="flex items-center gap-1.5 text-success bg-success/10 px-3 py-1.5 rounded-full border border-success/20 font-bold">
                    <Truck size={14} />
                    Free Delivery
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
