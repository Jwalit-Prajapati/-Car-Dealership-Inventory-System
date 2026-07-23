import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { list, search } from '../api/vehicles';
import { ApiError } from '../api/client';
import { useToast } from '../components/ToastProvider';
import { useAuth } from '../context/AuthContext';
import SearchFilterBar from '../components/SearchFilterBar';
import VehicleGrid from '../components/VehicleGrid';
import PaginationControls from '../components/PaginationControls';
import PurchaseModal from '../components/PurchaseModal';

const PAGE_SIZE = 12;
const EMPTY_PAGE = { content: [], totalElements: 0, totalPages: 0, number: 0, size: PAGE_SIZE };

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

function hasActiveFilters(filters) {
  return Object.values(filters).some((value) => value !== undefined && value !== '');
}

export default function DashboardPage() {
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [vehiclePage, setVehiclePage] = useState(EMPTY_PAGE);
  const [loading, setLoading] = useState(true);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const { showError, showSuccess } = useToast();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;

    async function fetchVehicles() {
      setLoading(true);
      try {
        const data = hasActiveFilters(filters)
          ? await search({ ...filters, page, size: PAGE_SIZE })
          : await list({ page, size: PAGE_SIZE });
        if (!cancelled) setVehiclePage(data);
      } catch (error) {
        if (!cancelled) {
          const message = error instanceof ApiError ? error.message : 'Unable to load vehicles.';
          showError(message);
          setVehiclePage(EMPTY_PAGE);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchVehicles();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters, page]);

  function handleSearch(nextFilters) {
    setFilters(nextFilters);
    setPage(0);
  }

  function handleClear() {
    setFilters({});
    setPage(0);
  }

  function handlePurchaseClick(vehicle) {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    setSelectedVehicle(vehicle);
  }

  function handlePurchaseSuccess(result) {
    setVehiclePage((current) => ({
      ...current,
      content: current.content.map((vehicle) =>
        vehicle.id === result.vehicleId
          ? { ...vehicle, quantity: vehicle.quantity - result.quantityPurchased }
          : vehicle,
      ),
    }));
    showSuccess(
      `Purchased ${result.quantityPurchased} × ${result.vehicleName} for ${currencyFormatter.format(
        result.purchasedPrice * result.quantityPurchased,
      )}.`,
    );
    setSelectedVehicle(null);
  }

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-semibold">Vehicle Dashboard</h1>
      <SearchFilterBar onSearch={handleSearch} onClear={handleClear} />
      <VehicleGrid vehicles={vehiclePage.content} loading={loading} onPurchase={handlePurchaseClick} />
      <PaginationControls
        page={vehiclePage.number}
        totalPages={vehiclePage.totalPages}
        totalElements={vehiclePage.totalElements}
        onPageChange={setPage}
      />
      {selectedVehicle && (
        <PurchaseModal
          vehicle={selectedVehicle}
          onClose={() => setSelectedVehicle(null)}
          onSuccess={handlePurchaseSuccess}
        />
      )}
    </div>
  );
}
