import React, { useEffect, useState, useContext } from 'react';
import { User, Mail, MapPin, Phone, Save, Camera } from 'lucide-react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Profile = () => {
  const { user } = useContext(AuthContext);
  const [profile, setProfile] = useState({
    username: '',
    email: '',
    address: '',
    phone: '',
    avatarUrl: ''
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get('/users/me');
        setProfile(response.data);
      } catch (err) {
        console.error("Failed to load profile:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      await api.put('/users/me', profile);
      setMessage('Profile updated successfully!');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setMessage('Failed to update profile.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64"><div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div></div>;

  return (
    <div className="max-w-3xl mx-auto pb-24">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">My Profile</h1>
        <p className="text-text-muted">Manage your personal information and delivery preferences.</p>
      </div>

      <div className="glass-panel p-8 animate-fade-in-up">
        {/* Avatar Section */}
        <div className="flex flex-col items-center mb-8 border-b border-glass-border pb-8">
          <div className="relative group cursor-pointer mb-4">
            <div className="w-32 h-32 rounded-full overflow-hidden bg-input-bg border-4 border-glass-border">
              {profile.avatarUrl ? (
                <img src={profile.avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-4xl font-bold text-white bg-gradient-to-tr from-primary to-blue-500">
                  {profile.username?.charAt(0).toUpperCase()}
                </div>
              )}
            </div>
          </div>
          <h2 className="text-2xl font-bold text-white">{profile.username}</h2>
          <p className="text-text-muted">{user?.roles?.includes('ROLE_ADMIN') ? 'Administrator' : 'Customer'}</p>
        </div>

        <form onSubmit={handleSave} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="input-group">
              <label className="input-label">Username (Read Only)</label>
              <div className="relative">
                <input type="text" className="input-field pl-12 bg-bg-darker text-text-muted" value={profile.username} disabled />
                <User className="absolute left-4 top-3.5 text-text-muted" size={20} />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Email Address</label>
              <div className="relative">
                <input type="email" name="email" className="input-field pl-12" value={profile.email || ''} onChange={handleChange} required />
                <Mail className="absolute left-4 top-3.5 text-text-muted" size={20} />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Phone Number</label>
              <div className="relative">
                <input type="text" name="phone" className="input-field pl-12" placeholder="e.g. +1 234 567 890" value={profile.phone || ''} onChange={handleChange} />
                <Phone className="absolute left-4 top-3.5 text-text-muted" size={20} />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Avatar Image URL</label>
              <div className="relative">
                <input type="text" name="avatarUrl" className="input-field pl-12" placeholder="https://..." value={profile.avatarUrl || ''} onChange={handleChange} />
                <Camera className="absolute left-4 top-3.5 text-text-muted" size={20} />
              </div>
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">Default Delivery Address</label>
            <div className="relative">
              <input type="text" name="address" className="input-field pl-12" placeholder="Enter your full address" value={profile.address || ''} onChange={handleChange} />
              <MapPin className="absolute left-4 top-3.5 text-text-muted" size={20} />
            </div>
          </div>

          {message && (
            <div className={`p-4 rounded-xl text-center font-medium ${message.includes('success') ? 'bg-success/20 text-success' : 'bg-error/20 text-error'}`}>
              {message}
            </div>
          )}

          <div className="flex justify-end pt-4">
            <button type="submit" disabled={saving} className="btn-primary w-full md:w-auto flex items-center justify-center gap-2 px-8">
              <Save size={20} />
              {saving ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;
