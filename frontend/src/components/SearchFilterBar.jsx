import { useState } from 'react';

const EMPTY_FILTERS = { make: '', model: '', category: '', minPrice: '', maxPrice: '' };

const FIELD_CLASS =
  'mt-1 w-full rounded border border-border-subtle bg-surface-1 px-3 py-2 text-sm text-text-primary placeholder:text-text-muted focus:border-[var(--accent)] focus:outline-none';

export default function SearchFilterBar({ initialFilters, onSearch, onClear }) {
  const [fields, setFields] = useState(() => ({ ...EMPTY_FILTERS, ...initialFilters }));

  function updateField(name, value) {
    setFields((current) => ({ ...current, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    onSearch({
      make: fields.make.trim(),
      model: fields.model.trim(),
      category: fields.category.trim(),
      minPrice: fields.minPrice,
      maxPrice: fields.maxPrice,
    });
  }

  function handleClear() {
    setFields(EMPTY_FILTERS);
    onClear();
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="grid grid-cols-2 gap-3 rounded-lg border border-border-subtle bg-surface-1 p-4 sm:grid-cols-3 lg:grid-cols-6"
    >
      <div>
        <label htmlFor="filter-make" className="block text-sm font-medium text-text-secondary">
          Make
        </label>
        <input
          id="filter-make"
          type="text"
          value={fields.make}
          onChange={(event) => updateField('make', event.target.value)}
          className={FIELD_CLASS}
        />
      </div>
      <div>
        <label htmlFor="filter-model" className="block text-sm font-medium text-text-secondary">
          Model
        </label>
        <input
          id="filter-model"
          type="text"
          value={fields.model}
          onChange={(event) => updateField('model', event.target.value)}
          className={FIELD_CLASS}
        />
      </div>
      <div>
        <label htmlFor="filter-category" className="block text-sm font-medium text-text-secondary">
          Category
        </label>
        <input
          id="filter-category"
          type="text"
          value={fields.category}
          onChange={(event) => updateField('category', event.target.value)}
          className={FIELD_CLASS}
        />
      </div>
      <div>
        <label htmlFor="filter-min-price" className="block text-sm font-medium text-text-secondary">
          Min price
        </label>
        <input
          id="filter-min-price"
          type="number"
          min="0"
          value={fields.minPrice}
          onChange={(event) => updateField('minPrice', event.target.value)}
          className={FIELD_CLASS}
        />
      </div>
      <div>
        <label htmlFor="filter-max-price" className="block text-sm font-medium text-text-secondary">
          Max price
        </label>
        <input
          id="filter-max-price"
          type="number"
          min="0"
          value={fields.maxPrice}
          onChange={(event) => updateField('maxPrice', event.target.value)}
          className={FIELD_CLASS}
        />
      </div>
      <div className="flex items-end gap-2">
        <button
          type="submit"
          className="w-full rounded bg-[var(--accent)] px-4 py-2 text-sm font-semibold text-white hover:bg-[var(--accent-hover)]"
        >
          Search
        </button>
        <button
          type="button"
          onClick={handleClear}
          className="w-full rounded border border-border-subtle px-4 py-2 text-sm text-text-secondary hover:bg-surface-2"
        >
          Clear
        </button>
      </div>
    </form>
  );
}
