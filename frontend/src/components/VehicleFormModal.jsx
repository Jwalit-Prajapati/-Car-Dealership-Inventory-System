import { useState } from 'react';
import { ApiError } from '../api/client';
import Spinner from './Spinner';

const FIELD_CLASS =
  'mt-1 w-full rounded border border-border-subtle bg-surface-2 px-3 py-2 text-sm text-text-primary focus:border-[var(--accent)] focus:outline-none';

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
  const [imageFile, setImageFile] = useState(null);

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
      await onSubmit(
        {
          make: fields.make.trim(),
          model: fields.model.trim(),
          category: fields.category.trim(),
          price: Number(fields.price),
          quantity: Number(fields.quantity),
        },
        imageFile,
      );
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unable to save vehicle. Please try again.';
      setSubmitError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div
      className="fixed inset-0 z-40 flex items-center justify-center bg-black/70 p-4"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        className="w-full max-w-md rounded-lg border border-border-subtle bg-surface-1 p-6 shadow-xl"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-2">
          <h2 className="text-lg font-semibold text-text-primary">
            {isEdit ? `Edit ${vehicle.make} ${vehicle.model}` : 'Add vehicle'}
          </h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="text-text-muted hover:text-text-primary"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit} noValidate className="mt-4 space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="vehicle-make" className="block text-sm font-medium text-text-secondary">
                Make
              </label>
              <input
                id="vehicle-make"
                type="text"
                value={fields.make}
                onChange={(event) => updateField('make', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.make && <p className="mt-1 text-sm text-red-400">{errors.make}</p>}
            </div>
            <div>
              <label htmlFor="vehicle-model" className="block text-sm font-medium text-text-secondary">
                Model
              </label>
              <input
                id="vehicle-model"
                type="text"
                value={fields.model}
                onChange={(event) => updateField('model', event.target.value)}
                className={FIELD_CLASS}
              />
              {errors.model && <p className="mt-1 text-sm text-red-400">{errors.model}</p>}
            </div>
          </div>

          <div>
            <label htmlFor="vehicle-category" className="block text-sm font-medium text-text-secondary">
              Category <span className="font-normal text-text-muted">(optional)</span>
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
              <label htmlFor="vehicle-price" className="block text-sm font-medium text-text-secondary">
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
              {errors.price && <p className="mt-1 text-sm text-red-400">{errors.price}</p>}
            </div>
            <div>
              <label htmlFor="vehicle-quantity" className="block text-sm font-medium text-text-secondary">
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
              {errors.quantity && <p className="mt-1 text-sm text-red-400">{errors.quantity}</p>}
            </div>
          </div>

          <div>
            <label htmlFor="vehicle-image" className="block text-sm font-medium text-text-secondary">
              Photo <span className="font-normal text-text-muted">(optional)</span>
            </label>
            <input
              id="vehicle-image"
              type="file"
              accept="image/jpeg,image/png,image/webp"
              onChange={(event) => setImageFile(event.target.files?.[0] ?? null)}
              className="mt-1 w-full text-sm text-text-secondary file:mr-3 file:rounded file:border-0 file:bg-surface-2 file:px-3 file:py-1.5 file:text-sm file:font-medium file:text-text-primary hover:file:bg-surface-3"
            />
            {imageFile && <p className="mt-1 text-xs text-text-muted">Selected: {imageFile.name}</p>}
          </div>

          {submitError && <p className="text-sm text-red-400">{submitError}</p>}

          <div className="flex gap-2">
            <button
              type="submit"
              disabled={submitting}
              className="flex w-full items-center justify-center gap-2 rounded bg-[var(--accent)] px-4 py-2 text-white hover:bg-[var(--accent-hover)] disabled:opacity-50"
            >
              {submitting && <Spinner />}
              {submitting ? 'Saving...' : isEdit ? 'Save changes' : 'Add vehicle'}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="w-full rounded border border-border-subtle px-4 py-2 text-text-secondary hover:bg-surface-2"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
