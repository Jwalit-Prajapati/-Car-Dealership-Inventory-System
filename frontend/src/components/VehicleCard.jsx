import { BASE_URL } from '../api/client';

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

const LOW_STOCK_THRESHOLD = 5;

function stockBadge(quantity) {
  if (quantity === 0) {
    return { label: 'Out of stock', className: 'bg-red-500/15 text-red-400' };
  }
  if (quantity < LOW_STOCK_THRESHOLD) {
    return { label: `Low stock — ${quantity} left`, className: 'bg-amber-500/15 text-amber-400' };
  }
  return { label: 'In stock', className: 'bg-emerald-500/15 text-emerald-400' };
}

export default function VehicleCard({ vehicle, onPurchase }) {
  const { make, model, category, price, quantity, imageUrl } = vehicle;
  const badge = stockBadge(quantity);
  const soldOut = quantity === 0;

  return (
    <div className="flex flex-col justify-between rounded-lg border border-border-subtle bg-surface-1 p-5 shadow-sm">
      <div>
        {imageUrl ? (
          <img
            src={`${BASE_URL}${imageUrl}`}
            alt={`${make} ${model}`}
            className="mb-4 h-56 w-full rounded object-cover"
          />
        ) : (
          <div className="mb-4 flex h-56 w-full items-center justify-center rounded bg-surface-2 text-sm text-text-muted">
            No photo
          </div>
        )}
        <div className="flex items-start justify-between gap-2">
          <h3 className="text-xl font-semibold text-text-primary">
            {make} {model}
          </h3>
          <span className={`whitespace-nowrap rounded-full px-2 py-1 text-xs font-medium ${badge.className}`}>
            {badge.label}
          </span>
        </div>
        {category && <p className="mt-1 text-sm text-text-muted">{category}</p>}
        <p className="mt-3 text-2xl font-bold text-text-primary">{currencyFormatter.format(price)}</p>
      </div>
      <button
        type="button"
        disabled={soldOut}
        onClick={() => onPurchase?.(vehicle)}
        className="mt-4 w-full rounded bg-[var(--accent)] px-4 py-2.5 text-white hover:bg-[var(--accent-hover)] disabled:cursor-not-allowed disabled:bg-surface-2 disabled:text-text-muted"
      >
        {soldOut ? 'Sold out' : 'Purchase'}
      </button>
    </div>
  );
}
