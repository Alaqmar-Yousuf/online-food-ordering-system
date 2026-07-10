import React, { useEffect, useState, useContext } from 'react';
import { Clock, CheckCircle, Package, Truck, Utensils, MapPin, Bike } from 'lucide-react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// Fix leaflet icon issue in React
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
});

const deliveryIcon = new L.Icon({
  iconUrl: 'https://cdn-icons-png.flaticon.com/512/2830/2830305.png', // custom truck icon
  iconSize: [40, 40],
  iconAnchor: [20, 40],
  popupAnchor: [0, -40]
});

const UserOrders = () => {
  const { user } = useContext(AuthContext);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await api.get('/orders/user');
        const sorted = response.data.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));
        setOrders(sorted);
      } catch (err) {
        console.error("Failed to fetch orders:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();

    // Setup WebSocket connection
    if (user?.id) {
      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        debug: function (str) {
          console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      client.onConnect = function (frame) {
        client.subscribe('/topic/orders/' + user.id, (message) => {
          if (message.body) {
            const updatedOrder = JSON.parse(message.body);
            setOrders(prevOrders => prevOrders.map(o => o.id === updatedOrder.id ? updatedOrder : o));
          }
        });
      };

      client.activate();
      return () => client.deactivate();
    }
  }, [user]);

  const getStatusIcon = (status) => {
    switch (status) {
      case 'PENDING': return <Clock className="text-yellow-400" size={24} />;
      case 'PREPARING': return <Package className="text-blue-400" size={24} />;
      case 'READY_FOR_PICKUP': return <Package className="text-orange-400" size={24} />;
      case 'DELIVERING': return <Truck className="text-purple-400 animate-bounce" size={24} />;
      case 'DELIVERED': return <CheckCircle className="text-success" size={24} />;
      case 'CANCELLED': return <Clock className="text-error" size={24} />;
      default: return <Clock className="text-text-muted" size={24} />;
    }
  };

  const getStatusColorClass = (status) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-400/10 text-yellow-400 border-yellow-400/20';
      case 'PREPARING': return 'bg-blue-400/10 text-blue-400 border-blue-400/20';
      case 'READY_FOR_PICKUP': return 'bg-orange-400/10 text-orange-400 border-orange-400/20';
      case 'DELIVERING': return 'bg-purple-400/10 text-purple-400 border-purple-400/20 shadow-[0_0_15px_rgba(168,85,247,0.4)]';
      case 'DELIVERED': return 'bg-success/10 text-success border-success/20';
      case 'CANCELLED': return 'bg-error/10 text-error border-error/20';
      default: return 'bg-glass-bg text-text-muted border-glass-border';
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;

  return (
    <div className="pb-24">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">My Orders</h1>
        <p className="text-text-muted">Track your recent orders and history in real-time.</p>
      </div>

      {orders.length === 0 ? (
        <div className="glass-panel p-12 text-center flex flex-col items-center">
          <Utensils size={48} className="text-text-muted mb-4" />
          <h2 className="text-xl font-bold text-white mb-2">No orders yet</h2>
          <p className="text-text-muted">You haven't placed any orders yet. Go explore some restaurants!</p>
        </div>
      ) : (
        <div className="space-y-6">
          {orders.map((order) => (
            <div key={order.id} className="glass-panel p-6 animate-fade-in-up transition-all duration-500">
              {/* Order Header */}
              <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 mb-6 border-b border-glass-border pb-4">
                <div>
                  <h2 className="text-xl font-bold text-white mb-1">{order.restaurant.name}</h2>
                  <p className="text-sm text-text-muted flex items-center gap-2">
                    <span>Order #{order.id}</span>
                    <span>•</span>
                    <span>{new Date(order.orderDate).toLocaleString()}</span>
                  </p>
                </div>
                
                <div className={`px-4 py-2 rounded-full border flex items-center gap-2 font-bold text-sm transition-colors duration-500 ${getStatusColorClass(order.status)}`}>
                  {getStatusIcon(order.status)}
                  {order.status.replace(/_/g, ' ')}
                </div>
              </div>

              {/* Live Map for Out For Delivery */}
              {order.status === 'DELIVERING' && (
                <div className="mb-6 rounded-xl overflow-hidden border border-glass-border h-64 relative z-0">
                  <div className="absolute top-2 left-2 z-[1000] bg-bg-darker/80 backdrop-blur-md px-3 py-1.5 rounded-lg border border-glass-border flex flex-col gap-1 text-white text-sm shadow-lg">
                    <div className="flex items-center gap-2 font-bold">
                      <span className="w-2 h-2 rounded-full bg-error animate-pulse"></span>
                      Live Tracking
                    </div>
                    {order.rider && (
                      <div className="text-xs text-text-muted mt-1 border-t border-glass-border pt-1 flex items-center gap-1">
                        <Bike size={12} /> Picked up by: <span className="text-primary font-medium">{order.rider.username}</span>
                      </div>
                    )}
                  </div>
                  <MapContainer center={[40.7128, -74.0060]} zoom={13} style={{ height: '100%', width: '100%' }}>
                    <TileLayer
                      url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
                      attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
                    />
                    <Marker position={[40.7128, -74.0060]} icon={deliveryIcon}>
                      <Popup>
                        <div className="text-center font-bold">Driver is on the way!</div>
                      </Popup>
                    </Marker>
                    <Marker position={[40.7200, -74.0100]}>
                      <Popup>Your Location</Popup>
                    </Marker>
                  </MapContainer>
                </div>
              )}

              {/* Order Items */}
              <div className="space-y-3 mb-6">
                {order.items.map((item) => (
                  <div key={item.id} className="flex justify-between items-center text-sm">
                    <div className="flex items-center gap-3">
                      <span className="w-6 h-6 bg-input-bg rounded flex items-center justify-center font-bold text-white border border-glass-border">{item.quantity}x</span>
                      <span className="text-white font-medium">{item.menuItem.name}</span>
                    </div>
                    <span className="text-text-muted font-medium">${(item.priceAtTimeOfOrder * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
              </div>

              {/* Order Footer */}
              <div className="flex justify-between items-center bg-input-bg p-4 rounded-xl border border-glass-border">
                <span className="text-text-muted font-medium">Total Paid</span>
                <span className="text-xl font-bold text-primary">${order.totalAmount.toFixed(2)}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default UserOrders;
