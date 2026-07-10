import React, { useContext } from 'react';
import { AuthContext } from './context/AuthContext';
import { CartContext } from './context/CartContext';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { Utensils, Home, ShoppingBag, Clock, LogOut, Settings, LayoutDashboard, Store, Bike } from 'lucide-react';

function App() {
  const { user, logout } = useContext(AuthContext);
  const { cartCount } = useContext(CartContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItemClass = ({ isActive }) => 
    `flex items-center gap-3 p-4 rounded-xl transition-all duration-300 ${
      isActive 
        ? 'bg-primary text-white shadow-[0_0_15px_rgba(255,43,94,0.3)]' 
        : 'text-text-muted hover:bg-glass-bg hover:text-white'
    }`;

  return (
    <div className="flex h-screen overflow-hidden bg-bg-darker">
      
      {/* Left Sidebar */}
      <aside className="w-64 flex-shrink-0 glass-panel m-4 rounded-3xl flex flex-col justify-between animate-slide-left z-10 border-r border-glass-border">
        
        {/* Brand */}
        <div className="p-8 flex items-center gap-3 text-primary border-b border-glass-border">
          <Utensils size={32} />
          <h1 className="text-2xl font-bold tracking-tight">FoodPanda</h1>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          {!user?.roles?.includes('ROLE_RIDER') && !user?.roles?.includes('ROLE_ADMIN') && (
            <NavLink to="/app/dashboard" className={navItemClass}>
              <Home size={22} />
              <span className="font-medium">Restaurants</span>
            </NavLink>
          )}
          
          {!user?.roles?.includes('ROLE_RESTAURANT') && !user?.roles?.includes('ROLE_RIDER') && !user?.roles?.includes('ROLE_ADMIN') && (
            <>
              <NavLink to="/app/checkout" className={navItemClass}>
                <div className="relative">
                  <ShoppingBag size={22} />
                  {cartCount > 0 && (
                    <span className="absolute -top-2 -right-2 bg-white text-primary text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center animate-pulseGlow">
                      {cartCount}
                    </span>
                  )}
                </div>
                <span className="font-medium">Cart</span>
              </NavLink>

              <NavLink to="/app/orders" className={navItemClass}>
                <Clock size={22} />
                <span className="font-medium">My Orders</span>
              </NavLink>
            </>
          )}

          <NavLink to="/app/profile" className={navItemClass}>
            <Settings size={22} />
            <span className="font-medium">Profile</span>
          </NavLink>

          {user?.roles?.includes('ROLE_ADMIN') && (
            <>
              <div className="mt-8 mb-4 px-4 text-xs font-bold text-text-muted uppercase tracking-wider">Admin Tools</div>
              <NavLink to="/app/admin" className={navItemClass}>
                <LayoutDashboard size={22} />
                <span className="font-medium">Dashboard</span>
              </NavLink>
            </>
          )}

          {user?.roles?.includes('ROLE_RESTAURANT') && (
            <>
              <div className="mt-8 mb-4 px-4 text-xs font-bold text-text-muted uppercase tracking-wider">Restaurant Tools</div>
              <NavLink to="/app/restaurant-dashboard" className={navItemClass}>
                <Store size={22} />
                <span className="font-medium">My Restaurant</span>
              </NavLink>
            </>
          )}
          {user?.roles?.includes('ROLE_RIDER') && (
            <>
              <div className="mt-8 mb-4 px-4 text-xs font-bold text-text-muted uppercase tracking-wider">Rider Tools</div>
              <NavLink to="/app/rider-dashboard" className={navItemClass}>
                <Bike size={22} />
                <span className="font-medium">My Deliveries</span>
              </NavLink>
            </>
          )}
        </nav>

        {/* User Footer */}
        <div className="p-4 border-t border-glass-border">
          <div className="flex items-center gap-3 p-3 bg-input-bg rounded-xl mb-4">
            <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-primary to-blue-500 flex items-center justify-center font-bold text-white">
              {user?.username?.charAt(0).toUpperCase() || 'U'}
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-sm font-semibold truncate text-white">{user?.username}</p>
              <p className="text-xs text-text-muted truncate">{user?.roles?.includes('ROLE_ADMIN') ? 'Admin' : 'User'}</p>
            </div>
          </div>
          
          <button 
            onClick={handleLogout} 
            className="flex items-center justify-center gap-2 w-full p-3 text-text-muted hover:text-error hover:bg-error/10 rounded-xl transition-colors font-medium"
          >
            <LogOut size={20} />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto p-4 sm:p-8 animate-fade-in-up relative z-0">
        <div className="max-w-6xl mx-auto">
          <Outlet />
        </div>
      </main>
      
    </div>
  );
}

export default App;
