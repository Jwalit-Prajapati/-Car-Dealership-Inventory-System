import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';

export default function PublicLayout() {
  return (
    <div className="zone-public min-h-screen bg-surface-0 text-text-primary">
      <Navbar />
      <Outlet />
    </div>
  );
}
