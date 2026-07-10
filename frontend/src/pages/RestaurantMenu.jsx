import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Minus, ShoppingBag } from 'lucide-react';
import api from '../services/api';
import { CartContext } from '../context/CartContext';

const RestaurantMenu = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const { cart, addToCart, decreaseQuantity, cartCount, cartTotal } = useContext(CartContext);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [restRes, menuRes] = await Promise.all([
          api.get(`/restaurants/${id}`),
          api.get(`/restaurants/${id}/menu`)
        ]);
        setRestaurant(restRes.data);
        setMenu(menuRes.data);
      } catch (err) {
        console.error("Failed to fetch menu:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;
  if (!restaurant) return <div>Restaurant not found</div>;

  return (
    <div className="pb-24">
      {/* Header */}
      <div className="relative h-64 rounded-3xl overflow-hidden mb-8 shadow-lg">
        <img 
          src={restaurant.imageUrl || "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1000&q=80"} 
          alt={restaurant.name}
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-bg-darker via-bg-darker/50 to-transparent"></div>
        
        <button 
          onClick={() => navigate('/app/dashboard')}
          className="absolute top-6 left-6 w-10 h-10 rounded-full bg-glass-bg backdrop-blur-md flex items-center justify-center text-white hover:bg-white hover:text-bg-dark transition-colors"
        >
          <ArrowLeft size={20} />
        </button>

        <div className="absolute bottom-6 left-6 md:left-8">
          <h1 className="text-4xl font-bold text-white mb-2">{restaurant.name}</h1>
          <p className="text-text-muted text-lg">{restaurant.address}</p>
        </div>
      </div>

      <h2 className="text-2xl font-bold mb-6 text-white flex items-center gap-2">
        <UtensilsIcon /> Menu
      </h2>

      {/* Menu Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {menu.map((item) => {
          const cartItem = cart.items.find(c => c.menuItem.id === item.id);
          const quantity = cartItem ? cartItem.quantity : 0;

          return (
            <div key={item.id} className="glass-panel p-4 flex gap-4 hover:border-primary/50 transition-colors">
              <div className="w-24 h-24 rounded-xl overflow-hidden flex-shrink-0 bg-input-bg">
                 <img 
                  src={item.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=200&q=80"} 
                  alt={item.name}
                  className="w-full h-full object-cover"
                />
              </div>
              
              <div className="flex-1 flex flex-col justify-between">
                <div>
                  <h3 className="font-bold text-lg text-white">{item.name}</h3>
                  <p className="text-text-muted text-sm line-clamp-2">{item.description}</p>
                </div>
                
                <div className="flex justify-between items-center mt-2">
                  <span className="font-bold text-primary">${item.price.toFixed(2)}</span>
                  
                  {/* Quantity Controls */}
                  <div className="flex items-center gap-3 bg-input-bg rounded-lg p-1 border border-glass-border">
                    <button 
                      onClick={() => decreaseQuantity(item.id)}
                      disabled={quantity === 0}
                      className={`w-8 h-8 flex items-center justify-center rounded-md transition-colors ${quantity > 0 ? 'text-white hover:bg-glass-bg' : 'text-text-muted opacity-50'}`}
                    >
                      <Minus size={16} />
                    </button>
                    <span className="w-4 text-center font-semibold">{quantity}</span>
                    <button 
                      onClick={() => addToCart(item, parseInt(id))}
                      className="w-8 h-8 flex items-center justify-center rounded-md text-white hover:bg-primary transition-colors"
                    >
                      <Plus size={16} />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Floating Cart Summary if items exist */}
      {cartCount > 0 && cart.restaurantId === parseInt(id) && (
        <div className="fixed bottom-8 left-1/2 transform -translate-x-1/2 w-full max-w-sm z-50 animate-fade-in-up">
          <button 
            onClick={() => navigate('/app/checkout')}
            className="w-full bg-primary text-white p-4 rounded-2xl shadow-2xl flex justify-between items-center hover:bg-primary-hover transition-colors font-semibold"
          >
            <div className="flex items-center gap-3">
              <div className="bg-white/20 w-8 h-8 rounded-full flex items-center justify-center">
                {cartCount}
              </div>
              <span>View Cart</span>
            </div>
            <span className="text-lg">${cartTotal.toFixed(2)}</span>
          </button>
        </div>
      )}
    </div>
  );
};

const UtensilsIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-primary"><path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2"/><path d="M7 2v20"/><path d="M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7"/></svg>
)

export default RestaurantMenu;
