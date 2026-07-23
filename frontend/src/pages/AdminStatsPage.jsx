import { useEffect, useState } from 'react';
import { Package, AlertTriangle, XCircle, DollarSign, ShoppingCart, Award } from 'lucide-react';
import { inventoryStats, salesStats } from '../api/stats';
import { ApiError } from '../api/client';
import { useToast } from '../components/ToastProvider';
import StatCard from '../components/StatCard';
import Spinner from '../components/Spinner';

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

export default function AdminStatsPage() {
  const [inventory, setInventory] = useState(null);
  const [sales, setSales] = useState(null);
  const [loading, setLoading] = useState(true);
  const { showError } = useToast();

  useEffect(() => {
    let cancelled = false;

    async function fetchStats() {
      setLoading(true);
      try {
        const [inventoryData, salesData] = await Promise.all([inventoryStats(), salesStats()]);
        if (!cancelled) {
          setInventory(inventoryData);
          setSales(salesData);
        }
      } catch (error) {
        if (!cancelled) {
          const message = error instanceof ApiError ? error.message : 'Unable to load stats.';
          showError(message);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchStats();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const mostPurchased = sales?.mostPurchasedVehicle;

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-semibold text-text-primary">Statistics</h1>

      {loading && (
        <div className="flex items-center gap-2 text-text-muted">
          <Spinner />
          Loading...
        </div>
      )}

      {!loading && inventory && sales && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <StatCard label="Total Vehicles" value={inventory.totalVehicles} icon={Package} />
          <StatCard label="Low Stock" value={inventory.lowStockVehicles} icon={AlertTriangle} />
          <StatCard label="Out of Stock" value={inventory.outOfStockVehicles} icon={XCircle} />
          <StatCard
            label="Total Revenue"
            value={currencyFormatter.format(sales.totalRevenue ?? 0)}
            icon={DollarSign}
          />
          <StatCard label="Total Sales Count" value={sales.totalSalesCount} icon={ShoppingCart} />
          <StatCard
            label="Most Purchased Vehicle"
            value={mostPurchased ? `${mostPurchased.make} ${mostPurchased.model}` : 'No sales yet'}
            icon={Award}
            highlight
          />
        </div>
      )}
    </div>
  );
}
