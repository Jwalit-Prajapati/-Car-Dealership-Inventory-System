import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Plus } from 'lucide-react';
import { list, search, create, update, remove, restock, uploadImage } from '../api/vehicles';
import { ApiError, BASE_URL } from '../api/client';
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
        className="w-16 rounded border border-border-subtle bg-surface-2 px-2 py-1 text-sm text-text-primary focus:border-[var(--accent)] focus:outline-none"
      />
      <button
        type="button"
        onClick={handleRestock}
        disabled={submitting}
        className="flex items-center gap-1 rounded border border-border-subtle px-2 py-1 text-sm text-text-secondary hover:bg-surface-2 disabled:opacity-50"
      >
        {submitting && <Spinner className="h-3 w-3" />}
        Restock
      </button>
    </div>
  );
}

export default function AdminVehiclesPage() {
  const [searchParams] = useSearchParams();
  const makeFilter = searchParams.get('make') || '';
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
      const data = makeFilter
        ? await search({ make: makeFilter, page, size: PAGE_SIZE })
        : await list({ page, size: PAGE_SIZE, sort: 'id' });
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
  }, [page, makeFilter]);

  async function handleFormSubmit(payload, imageFile) {
    let saved;
    if (formVehicle) {
      saved = await update(formVehicle.id, payload);
      showSuccess(`Updated ${payload.make} ${payload.model}.`);
    } else {
      saved = await create(payload);
      showSuccess(`Added ${payload.make} ${payload.model}.`);
    }
    if (imageFile) {
      saved = await uploadImage(saved.id, imageFile);
    }
    setFormVehicle(undefined);
    await fetchVehicles();
    return saved;
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
        <h1 className="text-2xl font-semibold text-text-primary">
          Car Listing
          {makeFilter && <span className="ml-2 text-base font-normal text-text-muted">— "{makeFilter}"</span>}
        </h1>
        <button
          type="button"
          onClick={() => setFormVehicle(null)}
          className="flex items-center gap-2 rounded bg-[var(--accent)] px-4 py-2 text-sm text-white hover:bg-[var(--accent-hover)]"
        >
          <Plus className="h-4 w-4" />
          Add vehicle
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-border-subtle">
        <table className="min-w-full divide-y divide-border-subtle text-sm">
          <thead className="bg-surface-1 text-left text-text-muted">
            <tr>
              <th className="px-4 py-3 font-medium">Photo</th>
              <th className="px-4 py-3 font-medium">Make</th>
              <th className="px-4 py-3 font-medium">Model</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Price</th>
              <th className="px-4 py-3 font-medium">Quantity</th>
              <th className="px-4 py-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border-subtle bg-surface-1">
            {loading && (
              <tr>
                <td colSpan={7} className="px-4 py-6 text-center text-text-muted">
                  <div className="flex items-center justify-center gap-2">
                    <Spinner />
                    Loading...
                  </div>
                </td>
              </tr>
            )}
            {!loading && vehiclePage.content.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-6 text-center text-text-muted">
                  No vehicles yet.
                </td>
              </tr>
            )}
            {!loading &&
              vehiclePage.content.map((vehicle) => (
                <tr key={vehicle.id}>
                  <td className="px-4 py-3">
                    {vehicle.imageUrl ? (
                      <img
                        src={`${BASE_URL}${vehicle.imageUrl}`}
                        alt={`${vehicle.make} ${vehicle.model}`}
                        className="h-10 w-14 rounded object-cover"
                      />
                    ) : (
                      <div className="flex h-10 w-14 items-center justify-center rounded bg-surface-2 text-xs text-text-muted">
                        —
                      </div>
                    )}
                  </td>
                  <td className="px-4 py-3 font-medium text-text-primary">{vehicle.make}</td>
                  <td className="px-4 py-3 text-text-secondary">{vehicle.model}</td>
                  <td className="px-4 py-3 text-text-muted">{vehicle.category || '—'}</td>
                  <td className="px-4 py-3 text-text-secondary">{currencyFormatter.format(vehicle.price)}</td>
                  <td className="px-4 py-3 text-text-secondary">{vehicle.quantity}</td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap items-center gap-2">
                      <button
                        type="button"
                        onClick={() => setFormVehicle(vehicle)}
                        className="rounded border border-border-subtle px-2 py-1 text-text-secondary hover:bg-surface-2"
                      >
                        Edit
                      </button>
                      <button
                        type="button"
                        onClick={() => setDeleteTarget(vehicle)}
                        className="rounded border border-red-900/50 px-2 py-1 text-red-400 hover:bg-red-500/10"
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
