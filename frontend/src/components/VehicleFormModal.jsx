import { useState } from 'react';
import { ApiError } from '../api/client';
import Spinner from './Spinner';

const FIELD_CLASS =
  'mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none';

function toFields(vehicle) {
  return {
    make: vehicle?.make ?? '',
    model: vehicle?.model ?? '',
    category: vehicle?.category ?? '',
    price: vehicle?.price ?? '',
    quantity: vehicle?.quantity ?? '',
  };
}

function validate(fields) {
  const errors = {};
  if (!fields.make.trim()) errors.make = 'Make is required.';
  if (!fields.model.trim()) errors.model = 'Model is required.';

  const price = Number(fields.price);
  if (fields.price === '' || Number.isNaN(price) || price <= 0) {
    errors.price = 'Price must be a positive number.';
  }

  const quantity = Number(fields.quantity);
  if (fields.quantity === '' || !Number.isInteger(quantity) || quantity < 0) {
    errors.quantity = 'Quantity must be a whole number of 0 or more.';
  }

  return errors;
}

export default function VehicleFormModal({ vehicle, onClose, onSubmit }) {
  const isEdit = Boolean(vehicle);
  const [fields, setFields] = useState(() => toFields(vehicle));
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState('');

  function updateField(name, value) {
    setFields((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const validationErrors = validate(fields);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    setSubmitError('');
    setSubmitting(true);
    try {
      await onSubmit({
        make: fields.make.trim(),
        model: fields.model.trim(),
        category: fields.category.trim(),
        price: Number(fields.price),
        quantity: Number(fields.quantity),
      });
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unable to save vehicle. Please try again.';
      setSubmitError(message);
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
        className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-2">
          <h2 className="text-lg font-semibold text-slate-900">
            {isEdit ? `Edit ${vehicle.make} ${vehicle.model}` : 'Add vehicle'}
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

        <form onSubmit={handleSubmit} noValidate className="mt-4 space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="vehicle-make" className="block text-sm font-medium text-slate-700">
                Make
              </label>
              <input
                id="vehicle-make"
                type="text"
                value={fields.make}
                onChange={(event) => updateField('make', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.make && <p className="mt-1 text-sm text-red-600">{errors.make}</p>}
            </div>
            <div>
              <label htmlFor="vehicle-model" className="block text-sm font-medium text-slate-700">
                Model
              </label>
              <input
                id="vehicle-model"
                type="text"
                value={fields.model}
                onChange={(event) => updateField('model', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.model && <p className="mt-1 text-sm text-red-600">{errors.model}</p>}
            </div>
          </div>

          <div>
            <label htmlFor="vehicle-category" className="block text-sm font-medium text-slate-700">
              Category <span className="font-normal text-slate-400">(optional)</span>
            </label>
            <input
              id="vehicle-category"
              type="text"
              value={fields.category}
              onChange={(event) => updateField('category', event.target.value)}
              className={FIELD_CLASS}
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="vehicle-price" className="block text-sm font-medium text-slate-700">
                Price
              </label>
              <input
                id="vehicle-price"
                type="number"
                min="0"
                step="0.01"
                value={fields.price}
                onChange={(event) => updateField('price', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.price && <p className="mt-1 text-sm text-red-600">{errors.price}</p>}
            </div>
            <div>
              <label htmlFor="vehicle-quantity" className="block text-sm font-medium text-slate-700">
                Quantity
              </label>
              <input
                id="vehicle-quantity"
                type="number"
                min="0"
                step="1"
                value={fields.quantity}
                onChange={(event) => updateField('quantity', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.quantity && <p className="mt-1 text-sm text-red-600">{errors.quantity}</p>}
            </div>
          </div>

          {submitError && <p className="text-sm text-red-600">{submitError}</p>}

          <div className="flex gap-2">
            <button
              type="submit"
              disabled={submitting}
              className="flex w-full items-center justify-center gap-2 rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800 disabled:opacity-50"
            >
              {submitting && <Spinner />}
              {submitting ? 'Saving...' : isEdit ? 'Save changes' : 'Add vehicle'}
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
