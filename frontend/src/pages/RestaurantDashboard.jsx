import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { Store, Utensils, Package, Edit, Trash2, Plus, Clock, CheckCircle, Truck, X } from 'lucide-react';

const RestaurantDashboard = () => {
  const { user } = useContext(AuthContext);
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('orders');
  const [isMenuModalOpen, setIsMenuModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);

  // Form state
  const [formData, setFormData] = useState({
    name: '', description: '', price: '', imageUrl: '', category: '', isAvailable: true
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const res = await api.get('/restaurants/my-restaurant');
        setRestaurant(res.data);
        
        if (res.data) {
          const [menuRes, ordersRes] = await Promise.all([
            api.get(`/restaurants/${res.data.id}/menu`),
            api.get('/orders/restaurant')
          ]);
          setMenu(menuRes.data);
          setOrders(ordersRes.data.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate)));
        }
      } catch (err) {
        console.error("Failed to fetch dashboard data:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  useEffect(() => {
    if (restaurant?.id) {
      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        reconnectDelay: 5000,
      });

      client.onConnect = function () {
        client.subscribe('/topic/restaurant/' + restaurant.id, (message) => {
          if (message.body) {
            const updatedOrder = JSON.parse(message.body);
            setOrders(prevOrders => {
              const exists = prevOrders.find(o => o.id === updatedOrder.id);
              if (exists) {
                return prevOrders.map(o => o.id === updatedOrder.id ? updatedOrder : o).sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));
              }
              return [updatedOrder, ...prevOrders];
            });
          }
        });
      };

      client.activate();
      return () => client.deactivate();
    }
  }, [restaurant]);

  const handleStatusUpdate = async (orderId, newStatus) => {
    try {
      await api.put(`/orders/${orderId}/restaurant-status?status=${newStatus}`);
      // Optimistic update
      setOrders(orders.map(o => o.id === orderId ? { ...o, status: newStatus } : o));
    } catch (err) {
      console.error("Failed to update status:", err);
    }
  };

  const handleMenuSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await api.put(`/restaurants/menu/${editingItem.id}`, formData);
      } else {
        await api.post('/restaurants/menu', formData);
      }
      
      // Refresh menu
      const menuRes = await api.get(`/restaurants/${restaurant.id}/menu`);
      setMenu(menuRes.data);
      closeModal();
    } catch (err) {
      console.error("Failed to save menu item:", err);
    }
  };

  const handleDeleteMenu = async (id) => {
    if (window.confirm("Are you sure you want to delete this item?")) {
      try {
        await api.delete(`/restaurants/menu/${id}`);
        setMenu(menu.filter(item => item.id !== id));
      } catch (err) {
        console.error("Failed to delete menu item:", err);
      }
    }
  };

  const toggleAvailability = async (item) => {
    try {
      await api.put(`/restaurants/menu/${item.id}`, { ...item, isAvailable: !item.isAvailable });
      setMenu(menu.map(m => m.id === item.id ? { ...m, isAvailable: !m.isAvailable } : m));
    } catch (err) {
      console.error("Failed to toggle availability:", err);
    }
  };

  const openModal = (item = null) => {
    setEditingItem(item);
    if (item) {
      setFormData({
        name: item.name, description: item.description || '', price: item.price,
        imageUrl: item.imageUrl || '', category: item.category, isAvailable: item.isAvailable
      });
    } else {
      setFormData({ name: '', description: '', price: '', imageUrl: '', category: 'General', isAvailable: true });
    }
    setIsMenuModalOpen(true);
  };

  const closeModal = () => {
    setIsMenuModalOpen(false);
    setEditingItem(null);
  };

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;

  if (!restaurant) return (
    <div className="glass-panel p-12 text-center">
      <Store size={48} className="text-error mx-auto mb-4" />
      <h2 className="text-xl font-bold text-white mb-2">Access Denied</h2>
      <p className="text-text-muted">You do not have a restaurant assigned to this account.</p>
    </div>
  );

  return (
    <div className="pb-24">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between md:items-end mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Restaurant Dashboard</h1>
          <p className="text-primary font-medium text-lg flex items-center gap-2"><Store size={20} /> {restaurant.name}</p>
        </div>
        
        {/* Tabs */}
        <div className="flex bg-input-bg p-1 rounded-xl border border-glass-border w-max">
          <button 
            onClick={() => setActiveTab('orders')}
            className={`px-6 py-2 rounded-lg font-medium transition-all ${activeTab === 'orders' ? 'bg-primary text-white shadow-lg' : 'text-text-muted hover:text-white'}`}
          >
            Live Orders
          </button>
          <button 
            onClick={() => setActiveTab('menu')}
            className={`px-6 py-2 rounded-lg font-medium transition-all ${activeTab === 'menu' ? 'bg-primary text-white shadow-lg' : 'text-text-muted hover:text-white'}`}
          >
            Menu Management
          </button>
        </div>
      </div>

      {activeTab === 'orders' && (
        <div className="animate-fade-in-up">
          <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Package className="text-primary" /> Active Orders
          </h2>
          
          {orders.length === 0 ? (
             <div className="glass-panel p-12 text-center text-text-muted">No orders yet. They will appear here in real-time.</div>
          ) : (
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
              {orders.map(order => (
                <div key={order.id} className="glass-panel p-6 border-l-4" style={{borderLeftColor: order.status === 'PENDING' ? '#facc15' : order.status === 'PREPARING' ? '#60a5fa' : order.status === 'READY_FOR_PICKUP' ? '#fb923c' : order.status === 'DELIVERING' ? '#c084fc' : order.status === 'DELIVERED' ? '#4ade80' : '#f87171'}}>
                  <div className="flex justify-between items-start mb-4 border-b border-glass-border pb-4">
                    <div>
                      <h3 className="font-bold text-white text-lg">Order #{order.id}</h3>
                      <p className="text-sm text-text-muted">{new Date(order.orderDate).toLocaleString()}</p>
                      <p className="text-sm font-medium mt-1 text-white">Customer: {order.user.username}</p>
                    </div>
                    <div className="text-right">
                      <span className="font-bold text-xl text-primary">${order.totalAmount.toFixed(2)}</span>
                      <div className="mt-1 text-xs px-2 py-1 bg-input-bg rounded-md text-white font-bold">{order.status}</div>
                    </div>
                  </div>
                  
                  <div className="mb-6 bg-input-bg p-3 rounded-lg border border-glass-border max-h-40 overflow-y-auto">
                    {order.items.map(item => (
                      <div key={item.id} className="flex justify-between text-sm mb-1">
                        <span className="text-white"><span className="text-primary font-bold">{item.quantity}x</span> {item.menuItem.name}</span>
                      </div>
                    ))}
                  </div>
                  
                  <div className="flex flex-wrap gap-2">
                    {order.status === 'PENDING' && (
                      <button onClick={() => handleStatusUpdate(order.id, 'PREPARING')} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg font-semibold transition-colors flex justify-center items-center gap-2">
                        <Utensils size={18} /> Start Preparing
                      </button>
                    )}
                    {order.status === 'PREPARING' && (
                      <button onClick={() => handleStatusUpdate(order.id, 'READY_FOR_PICKUP')} className="flex-1 bg-orange-500 hover:bg-orange-600 text-white py-2 rounded-lg font-semibold transition-colors flex justify-center items-center gap-2">
                        <Package size={18} /> Ready for Pickup
                      </button>
                    )}
                    {order.status === 'READY_FOR_PICKUP' && (
                      <div className="flex-1 bg-orange-500/20 text-orange-400 py-2 rounded-lg font-semibold flex justify-center items-center gap-2 border border-orange-500/30">
                        <Clock size={18} /> Waiting for Rider
                      </div>
                    )}
                    {order.status === 'DELIVERING' && (
                      <div className="flex-1 bg-purple-500/20 text-purple-400 py-2 rounded-lg font-semibold flex justify-center items-center gap-2 border border-purple-500/30">
                        <Truck size={18} /> Rider on the way
                      </div>
                    )}
                    {(order.status === 'PENDING' || order.status === 'PREPARING') && (
                      <button onClick={() => handleStatusUpdate(order.id, 'CANCELLED')} className="px-4 py-2 bg-error/20 hover:bg-error text-error hover:text-white rounded-lg font-semibold transition-colors border border-error/50">
                        Cancel
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'menu' && (
        <div className="animate-fade-in-up">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              <Utensils className="text-primary" /> Menu Items
            </h2>
            <button onClick={() => openModal()} className="bg-primary hover:bg-primary-hover text-white px-4 py-2 rounded-xl font-semibold flex items-center gap-2 transition-colors">
              <Plus size={20} /> Add Item
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            {menu.map(item => (
              <div key={item.id} className={`glass-panel p-4 flex gap-4 transition-all ${!item.isAvailable ? 'opacity-60 grayscale' : ''}`}>
                <div className="w-20 h-20 rounded-xl overflow-hidden flex-shrink-0 bg-input-bg">
                  <img src={item.imageUrl} alt={item.name} className="w-full h-full object-cover" />
                </div>
                <div className="flex-1 flex flex-col justify-between">
                  <div>
                    <h3 className="font-bold text-white leading-tight">{item.name}</h3>
                    <p className="text-xs text-text-muted">{item.category}</p>
                    <span className="font-bold text-primary mt-1 block">${item.price.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between items-center mt-2 pt-2 border-t border-glass-border">
                    <label className="flex items-center cursor-pointer">
                      <div className="relative">
                        <input type="checkbox" className="sr-only" checked={item.isAvailable} onChange={() => toggleAvailability(item)} />
                        <div className={`block w-10 h-6 rounded-full transition-colors ${item.isAvailable ? 'bg-primary' : 'bg-input-bg border border-glass-border'}`}></div>
                        <div className={`dot absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform ${item.isAvailable ? 'transform translate-x-4' : ''}`}></div>
                      </div>
                      <span className="ml-2 text-xs font-medium text-text-muted">{item.isAvailable ? 'Available' : 'Hidden'}</span>
                    </label>
                    <div className="flex gap-2">
                      <button onClick={() => openModal(item)} className="p-1.5 bg-input-bg hover:bg-white/10 text-white rounded-md transition-colors"><Edit size={16} /></button>
                      <button onClick={() => handleDeleteMenu(item.id)} className="p-1.5 bg-error/20 hover:bg-error text-error hover:text-white rounded-md transition-colors"><Trash2 size={16} /></button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Modal for Menu Item */}
      {isMenuModalOpen && (
        <div className="fixed inset-0 bg-bg-dark/80 backdrop-blur-sm z-[100] flex items-center justify-center p-4">
          <div className="glass-panel w-full max-w-lg p-6 rounded-2xl border border-glass-border animate-fade-in-up">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold text-white">{editingItem ? 'Edit Menu Item' : 'Add Menu Item'}</h2>
              <button onClick={closeModal} className="text-text-muted hover:text-white"><X size={24} /></button>
            </div>
            
            <form onSubmit={handleMenuSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-text-muted mb-1">Name</label>
                <input type="text" required value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="w-full bg-input-bg border border-glass-border rounded-xl p-3 text-white focus:border-primary focus:outline-none" />
              </div>
              <div className="flex gap-4">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-text-muted mb-1">Price ($)</label>
                  <input type="number" step="0.01" required value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})} className="w-full bg-input-bg border border-glass-border rounded-xl p-3 text-white focus:border-primary focus:outline-none" />
                </div>
                <div className="flex-1">
                  <label className="block text-sm font-medium text-text-muted mb-1">Category</label>
                  <input type="text" required value={formData.category} onChange={e => setFormData({...formData, category: e.target.value})} className="w-full bg-input-bg border border-glass-border rounded-xl p-3 text-white focus:border-primary focus:outline-none" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-text-muted mb-1">Description</label>
                <textarea rows="2" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} className="w-full bg-input-bg border border-glass-border rounded-xl p-3 text-white focus:border-primary focus:outline-none"></textarea>
              </div>
              <div>
                <label className="block text-sm font-medium text-text-muted mb-1">Image URL</label>
                <input type="url" value={formData.imageUrl} onChange={e => setFormData({...formData, imageUrl: e.target.value})} className="w-full bg-input-bg border border-glass-border rounded-xl p-3 text-white focus:border-primary focus:outline-none" />
              </div>
              
              <button type="submit" className="w-full bg-primary hover:bg-primary-hover text-white py-3 rounded-xl font-bold transition-colors mt-6">
                {editingItem ? 'Save Changes' : 'Create Item'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default RestaurantDashboard;
