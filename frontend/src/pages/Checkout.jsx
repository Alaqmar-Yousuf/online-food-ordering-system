import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag, ArrowRight, CreditCard, Trash2, CheckCircle } from 'lucide-react';
import { CartContext } from '../context/CartContext';
import api from '../services/api';

const Checkout = () => {
  const { cart, removeFromCart, cartTotal, clearCart } = useContext(CartContext);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handlePlaceOrder = async () => {
    if (cart.items.length === 0) return;
    
    setLoading(true);
    setError('');
    
    try {
      const orderRequest = {
        restaurantId: cart.restaurantId,
        items: cart.items.map(item => ({
          menuItemId: item.menuItem.id,
          quantity: item.quantity
        }))
      };

      await api.post('/orders', orderRequest);
      setSuccess(true);
      clearCart();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="flex flex-col items-center justify-center h-full py-20 animate-fade-in-up">
        <div className="w-24 h-24 bg-success/20 text-success rounded-full flex items-center justify-center mb-6">
          <CheckCircle size={48} />
        </div>
        <h1 className="text-3xl font-bold text-white mb-2">Order Confirmed!</h1>
        <p className="text-text-muted mb-8 text-center max-w-md">
          Your delicious food is being prepared. You can track its status in the Orders tab.
        </p>
        <button 
          onClick={() => navigate('/app/orders')}
          className="btn-primary max-w-xs"
        >
          View My Orders
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">Checkout</h1>
        <p className="text-text-muted">Review your items and complete your order.</p>
      </div>

      {error && (
        <div className="bg-error/20 border border-error text-error p-4 rounded-xl mb-6 flex items-center justify-center">
          {error}
        </div>
      )}

      {cart.items.length === 0 ? (
        <div className="glass-panel p-12 text-center flex flex-col items-center">
          <div className="w-20 h-20 bg-glass-bg rounded-full flex items-center justify-center text-text-muted mb-4">
            <ShoppingBag size={32} />
          </div>
          <h2 className="text-xl font-bold text-white mb-2">Your cart is empty</h2>
          <p className="text-text-muted mb-6">Looks like you haven't added any items yet.</p>
          <button 
            onClick={() => navigate('/app/dashboard')}
            className="btn-primary max-w-xs"
          >
            Browse Restaurants
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Cart Items */}
          <div className="lg:col-span-2 space-y-4">
            <h2 className="text-xl font-semibold text-white mb-4">Order Summary</h2>
            {cart.items.map((item) => (
              <div key={item.menuItem.id} className="glass-panel p-4 flex items-center justify-between animate-slide-left">
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 rounded-lg bg-input-bg flex items-center justify-center overflow-hidden">
                    <img 
                      src={item.menuItem.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=100&q=80"} 
                      alt={item.menuItem.name} 
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <div>
                    <h3 className="font-bold text-white">{item.menuItem.name}</h3>
                    <p className="text-sm text-text-muted">Qty: {item.quantity} x ${item.menuItem.price.toFixed(2)}</p>
                  </div>
                </div>
                
                <div className="flex items-center gap-6">
                  <span className="font-bold text-primary text-lg">${(item.menuItem.price * item.quantity).toFixed(2)}</span>
                  <button 
                    onClick={() => removeFromCart(item.menuItem.id)}
                    className="w-8 h-8 rounded-md bg-error/10 text-error hover:bg-error hover:text-white flex items-center justify-center transition-colors"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Checkout Details */}
          <div className="lg:col-span-1">
            <div className="glass-panel p-6 sticky top-4">
              <h2 className="text-xl font-semibold text-white mb-6">Payment Details</h2>
              
              <div className="space-y-4 mb-6">
                <div className="flex justify-between text-text-muted">
                  <span>Subtotal</span>
                  <span>${cartTotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-text-muted">
                  <span>Delivery Fee</span>
                  <span>$0.00</span>
                </div>
                <div className="flex justify-between text-text-muted">
                  <span>Taxes (Included)</span>
                  <span>$0.00</span>
                </div>
                <div className="border-t border-glass-border pt-4 flex justify-between text-white font-bold text-lg mt-4">
                  <span>Total</span>
                  <span className="text-primary">${cartTotal.toFixed(2)}</span>
                </div>
              </div>

              <div className="bg-input-bg p-4 rounded-xl border border-glass-border mb-6 flex items-center gap-3">
                <CreditCard className="text-primary" size={24} />
                <div>
                  <p className="text-sm text-white font-medium">Credit Card</p>
                  <p className="text-xs text-text-muted">Ending in •••• 4242</p>
                </div>
              </div>

              <button 
                onClick={handlePlaceOrder}
                disabled={loading}
                className="w-full bg-primary text-white p-4 rounded-xl font-bold flex justify-center items-center gap-2 hover:bg-primary-hover transition-colors shadow-lg"
              >
                {loading ? 'Processing...' : (
                  <>
                    Place Order <ArrowRight size={18} />
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Checkout;
