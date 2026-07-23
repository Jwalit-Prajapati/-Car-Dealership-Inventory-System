import Spinner from './Spinner';

export default function ConfirmDialog({
  title,
  message,
  confirmLabel = 'Confirm',
  busy = false,
  onConfirm,
  onCancel,
}) {
  return (
    <div
      className="fixed inset-0 z-40 flex items-center justify-center bg-slate-900/50 p-4"
      onClick={onCancel}
    >
      <div
        role="alertdialog"
        aria-modal="true"
        className="w-full max-w-sm rounded-lg bg-white p-6 shadow-xl"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 className="text-lg font-semibold text-slate-900">{title}</h2>
        <p className="mt-2 text-sm text-slate-600">{message}</p>
        <div className="mt-6 flex gap-2">
          <button
            type="button"
            disabled={busy}
            onClick={onConfirm}
            className="flex w-full items-center justify-center gap-2 rounded bg-red-600 px-4 py-2 text-white hover:bg-red-500 disabled:opacity-50"
          >
            {busy && <Spinner />}
            {busy ? 'Please wait...' : confirmLabel}
          </button>
          <button
            type="button"
            disabled={busy}
            onClick={onCancel}
            className="w-full rounded border border-slate-300 px-4 py-2 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
