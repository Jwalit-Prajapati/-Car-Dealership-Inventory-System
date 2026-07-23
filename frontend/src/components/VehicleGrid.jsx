import VehicleCard from './VehicleCard';

function SkeletonCard() {
  return (
    <div className="animate-pulse rounded-lg border border-border-subtle bg-surface-1 p-5 shadow-sm">
      <div className="mb-3 h-56 w-full rounded bg-surface-2" />
      <div className="flex items-start justify-between gap-2">
        <div className="h-5 w-2/3 rounded bg-surface-2" />
        <div className="h-5 w-16 rounded-full bg-surface-2" />
      </div>
      <div className="mt-2 h-4 w-1/3 rounded bg-surface-2" />
      <div className="mt-4 h-6 w-1/4 rounded bg-surface-2" />
      <div className="mt-4 h-9 w-full rounded bg-surface-2" />
    </div>
  );
}

export default function VehicleGrid({ vehicles, loading, onPurchase }) {
  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, index) => (
          <SkeletonCard key={index} />
        ))}
      </div>
    );
  }

  if (vehicles.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-border-subtle p-12 text-center text-text-muted">
        No vehicles match your filters.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      {vehicles.map((vehicle) => (
        <VehicleCard key={vehicle.id} vehicle={vehicle} onPurchase={onPurchase} />
      ))}
    </div>
  );
}
