import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { isAuthenticated, isAdmin, fullName, logout } = useAuth();

  return (
    <nav className="flex items-center justify-between px-6 py-4 bg-slate-900 text-white">
      <Link to="/" className="font-semibold text-lg">
        Vehicle Inventory
      </Link>
      <div className="flex items-center gap-4">
        {isAuthenticated ? (
          <>
            {isAdmin && (
              <>
                <Link to="/admin/vehicles" className="hover:underline">
                  Manage Vehicles
                </Link>
                <Link to="/admin/stats" className="hover:underline">
                  Stats
                </Link>
              </>
            )}
            <span>Welcome, {fullName}</span>
            <button
              type="button"
              onClick={logout}
              className="rounded bg-slate-700 px-3 py-1 hover:bg-slate-600"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="hover:underline">
              Login
            </Link>
            <Link to="/register" className="hover:underline">
              Register
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}
