import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

function readStoredAuth() {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');
  const fullName = localStorage.getItem('fullName');
  if (!token) return { token: null, role: null, fullName: null };
  return { token, role, fullName };
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(readStoredAuth);

  function login({ token, role, fullName }) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('fullName', fullName);
    setAuth({ token, role, fullName });
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('fullName');
    setAuth({ token: null, role: null, fullName: null });
  }

  const value = {
    ...auth,
    isAuthenticated: !!auth.token,
    isAdmin: auth.role === 'ADMIN',
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
