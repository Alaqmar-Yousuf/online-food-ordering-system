import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Utensils, Mail, Lock, LogIn } from 'lucide-react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', formData);
      login(response.data, response.data.accessToken || response.data.token);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-screen w-full">
      <div className="glass-panel p-8 w-full max-w-md animate-fade-in-up">
        <div className="text-center mb-6">
          <div className="flex justify-center items-center gap-2 mb-2 text-primary">
            <Utensils size={36} />
            <h1 className="text-3xl font-bold">FoodPanda</h1>
          </div>
          <p className="text-text-muted">Welcome back! Please login to your account.</p>
        </div>

        {error && (
          <div className="bg-error/20 border border-error text-error p-3 rounded-lg mb-4 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label className="input-label">Username</label>
            <div className="relative">
              <input
                type="text"
                className="input-field pl-12"
                placeholder="Enter your username"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
              />
              <Mail className="absolute left-4 top-3.5 text-text-muted" size={20} />
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">Password</label>
            <div className="relative">
              <input
                type="password"
                className="input-field pl-12"
                placeholder="Enter your password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                required
              />
              <Lock className="absolute left-4 top-3.5 text-text-muted" size={20} />
            </div>
          </div>

          <button type="submit" className="btn-primary mt-4" disabled={loading}>
            {loading ? 'Logging in...' : (
              <>
                <LogIn size={20} />
                Sign In
              </>
            )}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-text-muted">
          Don't have an account? <Link to="/register" className="text-primary font-semibold">Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
