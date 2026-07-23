import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_LINK_CLASS = ({ isActive }) =>
  `hover:underline ${isActive ? 'font-semibold underline' : ''}`;

export default function Navbar() {
  const { isAuthenticated, isAdmin, fullName, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  function handleLogout() {
    setMenuOpen(false);
    logout();
    navigate('/');
  }

  const links = isAuthenticated ? (
    <>
      {isAdmin && (
        <>
          <NavLink to="/admin/vehicles" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
            Manage Vehicles
          </NavLink>
          <NavLink to="/admin/stats" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
            Stats
          </NavLink>
        </>
      )}
      <span>Welcome, {fullName}</span>
      <button
        type="button"
        onClick={handleLogout}
        className="rounded bg-slate-700 px-3 py-1 text-left hover:bg-slate-600"
      >
        Logout
      </button>
    </>
  ) : (
    <>
      <NavLink to="/login" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
        Login
      </NavLink>
      <NavLink to="/register" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
        Register
      </NavLink>
    </>
  );

  return (
    <nav className="bg-slate-900 text-white">
      <div className="flex items-center justify-between px-6 py-4">
        <NavLink to="/" className="text-lg font-semibold" onClick={() => setMenuOpen(false)}>
          Vehicle Inventory
        </NavLink>
        <div className="hidden items-center gap-4 sm:flex">{links}</div>
        <button
          type="button"
          onClick={() => setMenuOpen((open) => !open)}
          aria-label="Toggle menu"
          aria-expanded={menuOpen}
          className="rounded p-1 hover:bg-slate-800 sm:hidden"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            {menuOpen ? (
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            ) : (
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            )}
          </svg>
        </button>
      </div>
      {menuOpen && (
        <div className="flex flex-col gap-3 border-t border-slate-800 px-6 py-4 sm:hidden">{links}</div>
      )}
    </nav>
  );
}
