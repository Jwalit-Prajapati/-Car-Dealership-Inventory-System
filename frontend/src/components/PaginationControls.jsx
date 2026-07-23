const MAX_VISIBLE_PAGES = 5;

function visiblePageNumbers(page, totalPages) {
  const half = Math.floor(MAX_VISIBLE_PAGES / 2);
  let start = Math.max(0, page - half);
  const end = Math.min(totalPages - 1, start + MAX_VISIBLE_PAGES - 1);
  start = Math.max(0, end - MAX_VISIBLE_PAGES + 1);
  return Array.from({ length: end - start + 1 }, (_, index) => start + index);
}

export default function PaginationControls({ page, totalPages, totalElements, onPageChange }) {
  if (totalElements === 0) return null;

  return (
    <div className="flex flex-wrap items-center justify-between gap-3">
      <p className="text-sm text-text-muted">
        {totalElements} vehicle{totalElements === 1 ? '' : 's'}
        {totalPages > 1 && ` — page ${page + 1} of ${totalPages}`}
      </p>
      {totalPages > 1 && (
        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={() => onPageChange(page - 1)}
            disabled={page === 0}
            className="rounded border border-border-subtle px-3 py-1 text-sm text-text-secondary hover:bg-surface-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Prev
          </button>
          {visiblePageNumbers(page, totalPages).map((pageNumber) => (
            <button
              key={pageNumber}
              type="button"
              onClick={() => onPageChange(pageNumber)}
              className={`rounded px-3 py-1 text-sm ${
                pageNumber === page
                  ? 'bg-[var(--accent)] text-white'
                  : 'border border-border-subtle text-text-secondary hover:bg-surface-2'
              }`}
            >
              {pageNumber + 1}
            </button>
          ))}
          <button
            type="button"
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages - 1}
            className="rounded border border-border-subtle px-3 py-1 text-sm text-text-secondary hover:bg-surface-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
