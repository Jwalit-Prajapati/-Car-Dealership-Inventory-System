import { Route, Routes } from 'react-router-dom';
import PublicLayout from './layouts/PublicLayout';
import AdminLayout from './layouts/AdminLayout';
import HomePage from './pages/HomePage';
import VehiclesPage from './pages/VehiclesPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AdminOverviewPage from './pages/AdminOverviewPage';
import AdminVehiclesPage from './pages/AdminVehiclesPage';
import AdminStatsPage from './pages/AdminStatsPage';
import AdminRoute from './routes/AdminRoute';

function App() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/vehicles" element={<VehiclesPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>
      <Route element={<AdminRoute />}>
        <Route element={<AdminLayout />}>
          <Route path="/admin" element={<AdminOverviewPage />} />
          <Route path="/admin/stats" element={<AdminStatsPage />} />
          <Route path="/admin/vehicles" element={<AdminVehiclesPage />} />
        </Route>
      </Route>
    </Routes>
  );
}

export default App;
