import VehicleCard from './VehicleCard';

function SkeletonCard() {
  return (
    <div className="animate-pulse rounded-lg border border-slate-200 p-4 shadow-sm">
      <div className="flex items-start justify-between gap-2">
        <div className="h-5 w-2/3 rounded bg-slate-200" />
        <div className="h-5 w-16 rounded-full bg-slate-200" />
      </div>
      <div className="mt-2 h-4 w-1/3 rounded bg-slate-200" />
      <div className="mt-4 h-6 w-1/4 rounded bg-slate-200" />
      <div className="mt-4 h-9 w-full rounded bg-slate-200" />
    </div>
  );
}

export default function VehicleGrid({ vehicles, loading, onPurchase }) {
  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {Array.from({ length: 8 }).map((_, index) => (
          <SkeletonCard key={index} />
        ))}
      </div>
    );
  }

  if (vehicles.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-slate-300 p-12 text-center text-slate-500">
        No vehicles match your filters.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {vehicles.map((vehicle) => (
        <VehicleCard key={vehicle.id} vehicle={vehicle} onPurchase={onPurchase} />
      ))}
    </div>
  );
}
