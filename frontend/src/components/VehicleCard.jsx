const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

const LOW_STOCK_THRESHOLD = 5;

function stockBadge(quantity) {
  if (quantity === 0) {
    return { label: 'Out of stock', className: 'bg-red-100 text-red-700' };
  }
  if (quantity < LOW_STOCK_THRESHOLD) {
    return { label: `Low stock — ${quantity} left`, className: 'bg-amber-100 text-amber-700' };
  }
  return { label: 'In stock', className: 'bg-emerald-100 text-emerald-700' };
}

export default function VehicleCard({ vehicle, onPurchase }) {
  const { make, model, category, price, quantity } = vehicle;
  const badge = stockBadge(quantity);
  const soldOut = quantity === 0;

  return (
    <div className="flex flex-col justify-between rounded-lg border border-slate-200 p-4 shadow-sm">
      <div>
        <div className="flex items-start justify-between gap-2">
          <h3 className="text-lg font-semibold text-slate-900">
            {make} {model}
          </h3>
          <span className={`whitespace-nowrap rounded-full px-2 py-1 text-xs font-medium ${badge.className}`}>
            {badge.label}
          </span>
        </div>
        {category && <p className="mt-1 text-sm text-slate-500">{category}</p>}
        <p className="mt-3 text-xl font-bold text-slate-900">{currencyFormatter.format(price)}</p>
      </div>
      <button
        type="button"
        disabled={soldOut}
        onClick={() => onPurchase?.(vehicle)}
        className="mt-4 w-full rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500 disabled:hover:bg-slate-300"
      >
        {soldOut ? 'Sold out' : 'Purchase'}
      </button>
    </div>
  );
}
