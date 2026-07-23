import { Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import AdminVehiclesPage from './pages/AdminVehiclesPage';
import AdminStatsPage from './pages/AdminStatsPage';
import AdminRoute from './routes/AdminRoute';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<AdminRoute />}>
          <Route path="/admin/vehicles" element={<AdminVehiclesPage />} />
          <Route path="/admin/stats" element={<AdminStatsPage />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
