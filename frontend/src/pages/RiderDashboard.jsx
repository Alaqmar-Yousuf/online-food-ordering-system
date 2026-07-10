import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { Bike, Package, MapPin, CheckCircle, Clock } from 'lucide-react';

const RiderDashboard = () => {
  const { user } = useContext(AuthContext);
  const [availableOrders, setAvailableOrders] = useState([]);
  const [myDeliveries, setMyDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('available');

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const [availableRes, deliveriesRes] = await Promise.all([
          api.get('/riders/orders/available'),
          api.get('/riders/orders/my-deliveries')
        ]);
        setAvailableOrders(availableRes.data);
        setMyDeliveries(deliveriesRes.data.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate)));
      } catch (err) {
        console.error("Failed to fetch rider orders:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
    });

    client.onConnect = function () {
      // Listen for new orders becoming available
      client.subscribe('/topic/riders/available', (message) => {
        if (message.body) {
          const updatedOrder = JSON.parse(message.body);
          
          if (updatedOrder.status === 'READY_FOR_PICKUP' && !updatedOrder.rider) {
            setAvailableOrders(prev => {
              const exists = prev.find(o => o.id === updatedOrder.id);
              if (exists) return prev;
              return [updatedOrder, ...prev];
            });
          } else {
            // If someone accepted it or status changed, remove from available
            setAvailableOrders(prev => prev.filter(o => o.id !== updatedOrder.id));
          }
        }
      });
    };

    client.activate();
    return () => client.deactivate();
  }, []);

  const handleAcceptOrder = async (orderId) => {
    try {
      const res = await api.put(`/riders/orders/${orderId}/accept`);
      setAvailableOrders(availableOrders.filter(o => o.id !== orderId));
      setMyDeliveries([res.data, ...myDeliveries]);
    } catch (err) {
      console.error("Failed to accept order:", err);
      alert(err.response?.data || "Failed to accept order. Someone else might have taken it.");
      // Refresh available orders to be safe
      const availableRes = await api.get('/riders/orders/available');
      setAvailableOrders(availableRes.data);
    }
  };

  const handleStatusUpdate = async (orderId, newStatus) => {
    try {
      const res = await api.put(`/riders/orders/${orderId}/status?status=${newStatus}`);
      setMyDeliveries(myDeliveries.map(o => o.id === orderId ? res.data : o));
    } catch (err) {
      console.error("Failed to update status:", err);
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;

  return (
    <div className="pb-24">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between md:items-end mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Rider Dashboard</h1>
          <p className="text-primary font-medium text-lg flex items-center gap-2"><Bike size={20} /> Welcome, {user?.username}</p>
        </div>
        
        {/* Tabs */}
        <div className="flex bg-input-bg p-1 rounded-xl border border-glass-border w-max">
          <button 
            onClick={() => setActiveTab('available')}
            className={`px-6 py-2 rounded-lg font-medium transition-all flex items-center gap-2 ${activeTab === 'available' ? 'bg-primary text-white shadow-lg' : 'text-text-muted hover:text-white'}`}
          >
            Available Orders
            {availableOrders.length > 0 && <span className="bg-white text-primary text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center animate-pulseGlow">{availableOrders.length}</span>}
          </button>
          <button 
            onClick={() => setActiveTab('my-deliveries')}
            className={`px-6 py-2 rounded-lg font-medium transition-all ${activeTab === 'my-deliveries' ? 'bg-primary text-white shadow-lg' : 'text-text-muted hover:text-white'}`}
          >
            My Deliveries
          </button>
        </div>
      </div>

      {activeTab === 'available' && (
        <div className="animate-fade-in-up">
          <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Package className="text-primary" /> Waiting for Pickup
          </h2>
          
          {availableOrders.length === 0 ? (
             <div className="glass-panel p-12 text-center text-text-muted">No orders available for pickup right now.</div>
          ) : (
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
              {availableOrders.map(order => (
                <div key={order.id} className="glass-panel p-6 border-l-4 border-yellow-400">
                  <div className="flex justify-between items-start mb-4 border-b border-glass-border pb-4">
                    <div>
                      <h3 className="font-bold text-white text-lg">Order #{order.id}</h3>
                      <p className="text-sm font-medium mt-1 text-white">Restaurant: {order.restaurant.name}</p>
                      <p className="text-sm text-text-muted flex items-center gap-1 mt-1"><MapPin size={14} /> {order.restaurant.address}</p>
                    </div>
                    <div className="text-right">
                      <span className="font-bold text-xl text-primary">${order.totalAmount.toFixed(2)}</span>
                    </div>
                  </div>
                  
                  <div className="mb-6 bg-input-bg p-3 rounded-lg border border-glass-border max-h-40 overflow-y-auto">
                    {order.items.map(item => (
                      <div key={item.id} className="flex justify-between text-sm mb-1">
                        <span className="text-white"><span className="text-primary font-bold">{item.quantity}x</span> {item.menuItem.name}</span>
                      </div>
                    ))}
                  </div>
                  
                  <button onClick={() => handleAcceptOrder(order.id)} className="w-full bg-success hover:bg-success/80 text-white py-3 rounded-xl font-bold transition-colors flex justify-center items-center gap-2">
                    <Bike size={20} /> Accept Delivery
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'my-deliveries' && (
        <div className="animate-fade-in-up">
          <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Bike className="text-primary" /> My Deliveries
          </h2>
          
          {myDeliveries.length === 0 ? (
             <div className="glass-panel p-12 text-center text-text-muted">You haven't accepted any deliveries yet.</div>
          ) : (
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
              {myDeliveries.map(order => (
                <div key={order.id} className={`glass-panel p-6 border-l-4 ${order.status === 'DELIVERING' ? 'border-purple-400' : order.status === 'DELIVERED' ? 'border-success opacity-70' : 'border-glass-border'}`}>
                  <div className="flex justify-between items-start mb-4 border-b border-glass-border pb-4">
                    <div>
                      <h3 className="font-bold text-white text-lg">Order #{order.id}</h3>
                      <p className="text-sm font-medium mt-1 text-white">From: {order.restaurant.name}</p>
                      <p className="text-sm text-text-muted flex items-center gap-1 mt-1"><MapPin size={14} /> To: {order.user.address || 'Customer Address'}</p>
                    </div>
                    <div className="text-right">
                      <div className={`mt-1 text-xs px-2 py-1 bg-input-bg rounded-md text-white font-bold`}>{order.status}</div>
                    </div>
                  </div>
                  
                  {order.status === 'DELIVERING' && (
                    <button onClick={() => handleStatusUpdate(order.id, 'DELIVERED')} className="w-full bg-purple-500 hover:bg-purple-600 text-white py-3 rounded-xl font-bold transition-colors flex justify-center items-center gap-2">
                      <CheckCircle size={20} /> Mark as Delivered
                    </button>
                  )}
                  {order.status === 'DELIVERED' && (
                    <div className="w-full bg-success/20 text-success py-3 rounded-xl font-bold flex justify-center items-center gap-2 border border-success/30">
                      <CheckCircle size={20} /> Delivery Completed
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default RiderDashboard;
