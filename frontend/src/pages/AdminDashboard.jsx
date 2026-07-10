import React, { useEffect, useState } from 'react';
import { Store, Users, Bike, DollarSign, Activity } from 'lucide-react';
import api from '../services/api';

const AdminDashboard = () => {
  const [revenue, setRevenue] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [riders, setRiders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const [revRes, cusRes, ridRes] = await Promise.all([
          api.get('/analytics/admin/revenue'),
          api.get('/analytics/admin/customers'),
          api.get('/analytics/admin/riders')
        ]);
        setRevenue(revRes.data);
        setCustomers(cusRes.data);
        setRiders(ridRes.data);
      } catch (err) {
        console.error("Failed to fetch analytics:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchAnalytics();
  }, []);

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;

  const totalSystemRevenue = revenue.reduce((sum, item) => sum + item.totalRevenue, 0);
  const totalSystemOrders = revenue.reduce((sum, item) => sum + item.totalOrders, 0);

  return (
    <div className="pb-24">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">Admin Analytics</h1>
        <p className="text-text-muted">Multi-table SQL aggregations on Revenue, Customers, and Riders.</p>
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div className="glass-panel p-6 flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-primary/20 flex items-center justify-center text-primary">
            <DollarSign size={32} />
          </div>
          <div>
            <p className="text-sm text-text-muted font-bold uppercase tracking-wider">Total System Revenue</p>
            <h2 className="text-3xl font-black text-white">${totalSystemRevenue.toFixed(2)}</h2>
          </div>
        </div>
        <div className="glass-panel p-6 flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-blue-500/20 flex items-center justify-center text-blue-400">
            <Activity size={32} />
          </div>
          <div>
            <p className="text-sm text-text-muted font-bold uppercase tracking-wider">Total Platform Orders</p>
            <h2 className="text-3xl font-black text-white">{totalSystemOrders}</h2>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Restaurant Revenue */}
        <div className="glass-panel overflow-hidden animate-fade-in-up" style={{ animationDelay: '0.1s' }}>
          <div className="p-4 border-b border-glass-border flex items-center gap-2">
            <Store className="text-primary" />
            <h2 className="font-bold text-white text-lg">Restaurant Revenue</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-input-bg border-b border-glass-border">
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Restaurant</th>
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Orders</th>
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Total Revenue</th>
                </tr>
              </thead>
              <tbody>
                {revenue.length === 0 && <tr><td colSpan="3" className="p-4 text-center text-text-muted">No revenue data.</td></tr>}
                {revenue.map((item, i) => (
                  <tr key={i} className="border-b border-glass-border hover:bg-glass-bg transition-colors">
                    <td className="p-4 font-medium text-white">{item.restaurantName}</td>
                    <td className="p-4 text-text-light">{item.totalOrders}</td>
                    <td className="p-4 font-bold text-primary">${item.totalRevenue.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Customer Activity */}
        <div className="glass-panel overflow-hidden animate-fade-in-up" style={{ animationDelay: '0.2s' }}>
          <div className="p-4 border-b border-glass-border flex items-center gap-2">
            <Users className="text-blue-400" />
            <h2 className="font-bold text-white text-lg">Customer Activity</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-input-bg border-b border-glass-border">
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Customer</th>
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Orders</th>
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Total Spent</th>
                </tr>
              </thead>
              <tbody>
                {customers.length === 0 && <tr><td colSpan="3" className="p-4 text-center text-text-muted">No customer data.</td></tr>}
                {customers.map((item, i) => (
                  <tr key={i} className="border-b border-glass-border hover:bg-glass-bg transition-colors">
                    <td className="p-4 font-medium text-white">{item.customerName}</td>
                    <td className="p-4 text-text-light">{item.totalOrders}</td>
                    <td className="p-4 font-bold text-blue-400">${item.totalSpent.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Rider Performance */}
        <div className="glass-panel overflow-hidden animate-fade-in-up lg:col-span-2" style={{ animationDelay: '0.3s' }}>
          <div className="p-4 border-b border-glass-border flex items-center gap-2">
            <Bike className="text-purple-400" />
            <h2 className="font-bold text-white text-lg">Top Riders (Delivered)</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-input-bg border-b border-glass-border">
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Rider Name</th>
                  <th className="p-4 text-text-muted font-medium text-xs uppercase tracking-wider">Completed Deliveries</th>
                </tr>
              </thead>
              <tbody>
                {riders.length === 0 && (
                  <tr><td colSpan="2" className="p-4 text-center text-text-muted">No deliveries completed yet.</td></tr>
                )}
                {riders.map((item, i) => (
                  <tr key={i} className="border-b border-glass-border hover:bg-glass-bg transition-colors">
                    <td className="p-4 font-medium text-white">{item.riderName}</td>
                    <td className="p-4 font-bold text-purple-400">{item.totalDeliveries}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
