import { useState } from 'react';
import { purchase } from '../api/purchases';
import { ApiError } from '../api/client';
import Spinner from './Spinner';

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

export default function PurchaseModal({ vehicle, onClose, onSuccess }) {
  const [quantity, setQuantity] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const maxQuantity = vehicle.quantity;

  function updateQuantity(next) {
    if (Number.isNaN(next)) return;
    setQuantity(Math.min(Math.max(Math.trunc(next), 1), maxQuantity));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > maxQuantity) {
      setError(`Enter a quantity between 1 and ${maxQuantity}.`);
      return;
    }

    setSubmitting(true);
    try {
      const result = await purchase({ vehicleId: vehicle.id, quantity });
      onSuccess(result);
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unable to complete purchase. Please try again.';
      setError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div
      className="fixed inset-0 z-40 flex items-center justify-center bg-slate-900/50 p-4"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        className="w-full max-w-sm rounded-lg bg-white p-6 shadow-xl"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-2">
          <h2 className="text-lg font-semibold text-slate-900">
            Purchase {vehicle.make} {vehicle.model}
          </h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="text-slate-400 hover:text-slate-600"
          >
            ×
          </button>
        </div>
        <p className="mt-1 text-sm text-slate-500">
          {currencyFormatter.format(vehicle.price)} each &middot; {maxQuantity} in stock
        </p>

        <form onSubmit={handleSubmit} noValidate className="mt-4 space-y-4">
          <div>
            <label htmlFor="purchase-quantity" className="block text-sm font-medium text-slate-700">
              Quantity
            </label>
            <div className="mt-1 flex items-center gap-2">
              <button
                type="button"
                onClick={() => updateQuantity(quantity - 1)}
                disabled={quantity <= 1}
                className="rounded border border-slate-300 px-3 py-2 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
              >
                −
              </button>
              <input
                id="purchase-quantity"
                type="number"
                min="1"
                max={maxQuantity}
                value={quantity}
                onChange={(event) => updateQuantity(Number(event.target.value))}
                className="w-full rounded border border-slate-300 px-3 py-2 text-center focus:border-slate-500 focus:outline-none"
              />
              <button
                type="button"
                onClick={() => updateQuantity(quantity + 1)}
                disabled={quantity >= maxQuantity}
                className="rounded border border-slate-300 px-3 py-2 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
              >
                +
              </button>
            </div>
            {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
          </div>

          <p className="text-sm font-medium text-slate-900">
            Total: {currencyFormatter.format(vehicle.price * quantity)}
          </p>

          <div className="flex gap-2">
            <button
              type="submit"
              disabled={submitting || maxQuantity === 0}
              className="flex w-full items-center justify-center gap-2 rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800 disabled:opacity-50"
            >
              {submitting && <Spinner />}
              {submitting ? 'Purchasing...' : 'Confirm purchase'}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="w-full rounded border border-slate-300 px-4 py-2 text-slate-700 hover:bg-slate-50"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
