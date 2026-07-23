import { useEffect, useState } from 'react';
import { list, create, update, remove, restock } from '../api/vehicles';
import { ApiError } from '../api/client';
import { useToast } from '../components/ToastProvider';
import PaginationControls from '../components/PaginationControls';
import VehicleFormModal from '../components/VehicleFormModal';
import ConfirmDialog from '../components/ConfirmDialog';
import Spinner from '../components/Spinner';

const PAGE_SIZE = 10;
const EMPTY_PAGE = { content: [], totalElements: 0, totalPages: 0, number: 0, size: PAGE_SIZE };

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

function RestockControl({ vehicle, onRestock }) {
  const [amount, setAmount] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { showError } = useToast();

  async function handleRestock() {
    const quantity = Number(amount);
    if (!Number.isInteger(quantity) || quantity <= 0) {
      showError('Enter a whole number greater than 0 to restock.');
      return;
    }
    setSubmitting(true);
    try {
      await onRestock(vehicle, quantity);
      setAmount('');
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unable to restock vehicle.';
      showError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex items-center gap-1">
      <input
        type="number"
        min="1"
        step="1"
        value={amount}
        onChange={(event) => setAmount(event.target.value)}
        placeholder="Qty"
        className="w-16 rounded border border-slate-300 px-2 py-1 text-sm focus:border-slate-500 focus:outline-none"
      />
      <button
        type="button"
        onClick={handleRestock}
        disabled={submitting}
        className="flex items-center gap-1 rounded border border-slate-300 px-2 py-1 text-sm text-slate-700 hover:bg-slate-50 disabled:opacity-50"
      >
        {submitting && <Spinner className="h-3 w-3" />}
        Restock
      </button>
    </div>
  );
}

export default function AdminVehiclesPage() {
  const [page, setPage] = useState(0);
  const [vehiclePage, setVehiclePage] = useState(EMPTY_PAGE);
  const [loading, setLoading] = useState(true);
  const [formVehicle, setFormVehicle] = useState(undefined);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const { showSuccess, showError } = useToast();

  async function fetchVehicles() {
    setLoading(true);
    try {
      const data = await list({ page, size: PAGE_SIZE, sort: 'id' });
      setVehiclePage(data);
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Unable to load vehicles.';
      showError(message);
      setVehiclePage(EMPTY_PAGE);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    (async () => {
      await fetchVehicles();
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  async function handleFormSubmit(payload) {
    if (formVehicle) {
      await update(formVehicle.id, payload);
      showSuccess(`Updated ${payload.make} ${payload.model}.`);
    } else {
      await create(payload);
      showSuccess(`Added ${payload.make} ${payload.model}.`);
    }
    setFormVehicle(undefined);
    await fetchVehicles();
  }

  async function handleRestock(vehicle, quantity) {
    await restock(vehicle.id, quantity);
    showSuccess(`Restocked ${quantity} × ${vehicle.make} ${vehicle.model}.`);
    await fetchVehicles();
  }

  async function handleDeleteConfirm() {
    setDeleting(true);
    try {
      await remove(deleteTarget.id);
      showSuccess(`Deleted ${deleteTarget.make} ${deleteTarget.model}.`);
      setDeleteTarget(null);
      if (vehiclePage.content.length === 1 && page > 0) {
        setPage((current) => current - 1);
      } else {
        await fetchVehicles();
      }
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Unable to delete vehicle.';
      showError(message);
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Admin: Vehicles</h1>
        <button
          type="button"
          onClick={() => setFormVehicle(null)}
          className="rounded bg-slate-900 px-4 py-2 text-sm text-white hover:bg-slate-800"
        >
          Add vehicle
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-slate-200">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Make</th>
              <th className="px-4 py-3 font-medium">Model</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Price</th>
              <th className="px-4 py-3 font-medium">Quantity</th>
              <th className="px-4 py-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-slate-500">
                  <div className="flex items-center justify-center gap-2">
                    <Spinner />
                    Loading...
                  </div>
                </td>
              </tr>
            )}
            {!loading && vehiclePage.content.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-slate-500">
                  No vehicles yet.
                </td>
              </tr>
            )}
            {!loading &&
              vehiclePage.content.map((vehicle) => (
                <tr key={vehicle.id}>
                  <td className="px-4 py-3 font-medium text-slate-900">{vehicle.make}</td>
                  <td className="px-4 py-3">{vehicle.model}</td>
                  <td className="px-4 py-3 text-slate-500">{vehicle.category || '—'}</td>
                  <td className="px-4 py-3">{currencyFormatter.format(vehicle.price)}</td>
                  <td className="px-4 py-3">{vehicle.quantity}</td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap items-center gap-2">
                      <button
                        type="button"
                        onClick={() => setFormVehicle(vehicle)}
                        className="rounded border border-slate-300 px-2 py-1 text-slate-700 hover:bg-slate-50"
                      >
                        Edit
                      </button>
                      <button
                        type="button"
                        onClick={() => setDeleteTarget(vehicle)}
                        className="rounded border border-red-200 px-2 py-1 text-red-600 hover:bg-red-50"
                      >
                        Delete
                      </button>
                      <RestockControl vehicle={vehicle} onRestock={handleRestock} />
                    </div>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      <PaginationControls
        page={vehiclePage.number}
        totalPages={vehiclePage.totalPages}
        totalElements={vehiclePage.totalElements}
        onPageChange={setPage}
      />

      {formVehicle !== undefined && (
        <VehicleFormModal
          vehicle={formVehicle}
          onClose={() => setFormVehicle(undefined)}
          onSubmit={handleFormSubmit}
        />
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete vehicle"
          message={`Delete ${deleteTarget.make} ${deleteTarget.model}? This can't be undone.`}
          confirmLabel="Delete"
          busy={deleting}
          onConfirm={handleDeleteConfirm}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </div>
  );
}
