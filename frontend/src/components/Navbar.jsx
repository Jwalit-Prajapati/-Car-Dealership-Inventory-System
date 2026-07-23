import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Menu, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const NAV_LINK_CLASS = ({ isActive }) =>
  `text-sm font-medium transition-colors ${
    isActive ? 'text-text-primary' : 'text-text-secondary hover:text-text-primary'
  }`;

const COMING_SOON = ['Sell', 'About', 'Contact'];

export default function Navbar() {
  const { isAuthenticated, isAdmin, fullName, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  function handleLogout() {
    setMenuOpen(false);
    logout();
    navigate('/');
  }

  const links = isAdmin ? null : (
    <>
      <NavLink to="/" end className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
        Home
      </NavLink>
      <NavLink to="/vehicles" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
        Buy
      </NavLink>
      {COMING_SOON.map((label) => (
        <span
          key={label}
          title="Coming soon"
          className="cursor-default text-sm font-medium text-text-muted"
        >
          {label}
        </span>
      ))}
    </>
  );

  const authLinks = isAuthenticated ? (
    <>
      {isAdmin && (
        <NavLink to="/admin" className={NAV_LINK_CLASS} onClick={() => setMenuOpen(false)}>
          Admin
        </NavLink>
      )}
      <span className="text-sm text-text-secondary">Welcome, {fullName}</span>
      <button
        type="button"
        onClick={handleLogout}
        className="rounded bg-surface-2 px-3 py-1.5 text-sm text-text-primary hover:bg-surface-3"
      >
        Logout
      </button>
    </>
  ) : (
    <>
      <NavLink
        to="/login"
        className="text-sm font-medium text-text-secondary hover:text-text-primary"
        onClick={() => setMenuOpen(false)}
      >
        Login
      </NavLink>
      <NavLink
        to="/register"
        className="rounded bg-[var(--accent)] px-4 py-1.5 text-sm font-semibold text-white hover:bg-[var(--accent-hover)]"
        onClick={() => setMenuOpen(false)}
      >
        Sign Up
      </NavLink>
    </>
  );

  return (
    <nav className="border-b border-border-subtle bg-surface-0">
      <div className="flex items-center justify-between px-6 py-4">
        <NavLink to="/" className="text-lg font-semibold text-text-primary" onClick={() => setMenuOpen(false)}>
          Vehicle Inventory
        </NavLink>
        <div className="hidden items-center gap-6 sm:flex">{links}</div>
        <div className="hidden items-center gap-3 sm:flex">{authLinks}</div>
        <button
          type="button"
          onClick={() => setMenuOpen((open) => !open)}
          aria-label="Toggle menu"
          aria-expanded={menuOpen}
          className="rounded p-1 text-text-primary hover:bg-surface-2 sm:hidden"
        >
          {menuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
        </button>
      </div>
      {menuOpen && (
        <div className="flex flex-col gap-3 border-t border-border-subtle px-6 py-4 sm:hidden">
          {links}
          {authLinks}
        </div>
      )}
    </nav>
  );
}
