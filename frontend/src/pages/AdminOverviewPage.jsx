import { useEffect, useState } from 'react';
import { Package, AlertTriangle, XCircle, DollarSign, ShoppingCart } from 'lucide-react';
import { inventoryStats, salesStats } from '../api/stats';
import { list } from '../api/vehicles';
import { ApiError, BASE_URL } from '../api/client';
import { useToast } from '../components/ToastProvider';
import StatCard from '../components/StatCard';
import Spinner from '../components/Spinner';

const RECENT_COUNT = 5;

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

const LOW_STOCK_THRESHOLD = 5;

function stockBadge(quantity) {
  if (quantity === 0) return { label: 'Out of stock', className: 'bg-red-500/15 text-red-400' };
  if (quantity < LOW_STOCK_THRESHOLD) return { label: 'Low stock', className: 'bg-amber-500/15 text-amber-400' };
  return { label: 'In stock', className: 'bg-emerald-500/15 text-emerald-400' };
}

function StockBreakdownBar({ total, low, out }) {
  const healthy = Math.max(total - low - out, 0);
  if (total === 0) {
    return <p className="text-sm text-text-muted">No vehicles in inventory yet.</p>;
  }

  const segments = [
    { label: 'Healthy stock', value: healthy, color: 'bg-emerald-500' },
    { label: 'Low stock', value: low, color: 'bg-amber-500' },
    { label: 'Out of stock', value: out, color: 'bg-red-500' },
  ].filter((segment) => segment.value > 0);

  return (
    <div>
      <div className="flex h-3 w-full gap-0.5 overflow-hidden rounded-full bg-surface-2">
        {segments.map((segment) => (
          <div
            key={segment.label}
            className={`${segment.color} first:rounded-l-full last:rounded-r-full`}
            style={{ width: `${(segment.value / total) * 100}%` }}
          />
        ))}
      </div>
      <div className="mt-3 flex flex-wrap gap-x-6 gap-y-2">
        {segments.map((segment) => (
          <div key={segment.label} className="flex items-center gap-2 text-sm">
            <span className={`h-2.5 w-2.5 rounded-full ${segment.color}`} />
            <span className="text-text-secondary">{segment.label}</span>
            <span className="font-medium text-text-primary">{segment.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function AdminOverviewPage() {
  const [inventory, setInventory] = useState(null);
  const [sales, setSales] = useState(null);
  const [recentVehicles, setRecentVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const { showError } = useToast();

  useEffect(() => {
    let cancelled = false;

    async function fetchOverview() {
      setLoading(true);
      try {
        const [inventoryData, salesData, vehiclePage] = await Promise.all([
          inventoryStats(),
          salesStats(),
          list({ page: 0, size: RECENT_COUNT, sort: 'id,desc' }),
        ]);
        if (!cancelled) {
          setInventory(inventoryData);
          setSales(salesData);
          setRecentVehicles(vehiclePage.content);
        }
      } catch (error) {
        if (!cancelled) {
          const message = error instanceof ApiError ? error.message : 'Unable to load dashboard data.';
          showError(message);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchOverview();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-semibold text-text-primary">Dashboard</h1>

      {loading && (
        <div className="flex items-center gap-2 text-text-muted">
          <Spinner />
          Loading...
        </div>
      )}

      {!loading && inventory && sales && (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            <StatCard label="Total Vehicles" value={inventory.totalVehicles} icon={Package} />
            <StatCard label="Low Stock" value={inventory.lowStockVehicles} icon={AlertTriangle} />
            <StatCard label="Out of Stock" value={inventory.outOfStockVehicles} icon={XCircle} />
            <StatCard
              label="Total Revenue"
              value={currencyFormatter.format(sales.totalRevenue ?? 0)}
              icon={DollarSign}
            />
            <StatCard label="Total Sales" value={sales.totalSalesCount} icon={ShoppingCart} />
          </div>

          <div className="rounded-lg border border-border-subtle bg-surface-1 p-4 shadow-sm">
            <h2 className="text-sm font-medium text-text-secondary">Inventory Health</h2>
            <div className="mt-4">
              <StockBreakdownBar
                total={inventory.totalVehicles}
                low={inventory.lowStockVehicles}
                out={inventory.outOfStockVehicles}
              />
            </div>
          </div>

          <div className="rounded-lg border border-border-subtle bg-surface-1 p-4 shadow-sm">
            <h2 className="text-sm font-medium text-text-secondary">Recent Car Listings</h2>
            {recentVehicles.length === 0 ? (
              <p className="mt-3 text-sm text-text-muted">No vehicles added yet.</p>
            ) : (
              <div className="mt-3 divide-y divide-border-subtle">
                {recentVehicles.map((vehicle) => {
                  const badge = stockBadge(vehicle.quantity);
                  return (
                    <div key={vehicle.id} className="flex items-center gap-3 py-3">
                      {vehicle.imageUrl ? (
                        <img
                          src={`${BASE_URL}${vehicle.imageUrl}`}
                          alt={`${vehicle.make} ${vehicle.model}`}
                          className="h-12 w-16 shrink-0 rounded object-cover"
                        />
                      ) : (
                        <div className="flex h-12 w-16 shrink-0 items-center justify-center rounded bg-surface-2 text-xs text-text-muted">
                          —
                        </div>
                      )}
                      <div className="min-w-0 flex-1">
                        <p className="truncate text-sm font-medium text-text-primary">
                          {vehicle.make} {vehicle.model}
                        </p>
                        <p className="text-xs text-text-muted">{vehicle.category || '—'}</p>
                      </div>
                      <p className="text-sm font-semibold text-text-primary">
                        {currencyFormatter.format(vehicle.price)}
                      </p>
                      <span
                        className={`whitespace-nowrap rounded-full px-2 py-1 text-xs font-medium ${badge.className}`}
                      >
                        {badge.label}
                      </span>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
