import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Utensils, User, Mail, Lock, UserPlus } from 'lucide-react';
import api from '../services/api';

const Register = () => {
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await api.post('/auth/register', formData);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-screen w-full">
      <div className="glass-panel p-8 w-full max-w-md animate-slide-left">
        <div className="text-center mb-6">
          <div className="flex justify-center items-center gap-2 mb-2 text-primary">
            <Utensils size={36} />
            <h1 className="text-3xl font-bold">Join Us</h1>
          </div>
          <p className="text-text-muted">Create a new account to start ordering.</p>
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
                placeholder="Choose a username"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
              />
              <User className="absolute left-4 top-3.5 text-text-muted" size={20} />
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">Email</label>
            <div className="relative">
              <input
                type="email"
                className="input-field pl-12"
                placeholder="Enter your email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
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
                placeholder="Create a password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                required
              />
              <Lock className="absolute left-4 top-3.5 text-text-muted" size={20} />
            </div>
          </div>

          <button type="submit" className="btn-primary mt-4" disabled={loading}>
            {loading ? 'Registering...' : (
              <>
                <UserPlus size={20} />
                Create Account
              </>
            )}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-text-muted">
          Already have an account? <Link to="/login" className="text-primary font-semibold">Login here</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
