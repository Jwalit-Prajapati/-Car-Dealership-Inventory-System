const VARIANT_STYLES = {
  success: 'bg-emerald-600',
  error: 'bg-red-600',
};
// Toasts float over both zones; keep neutral emerald/red rather than the zone accent
// so success/error meaning stays unambiguous regardless of red-accent vs purple-accent pages.

export default function Toast({ message, variant = 'success', onDismiss }) {
  return (
    <div
      role="alert"
      className={`flex items-center justify-between gap-4 rounded px-4 py-3 text-white shadow-lg ${VARIANT_STYLES[variant]}`}
    >
      <span className="text-sm">{message}</span>
      <button
        type="button"
        onClick={onDismiss}
        aria-label="Dismiss"
        className="text-white/80 hover:text-white"
      >
        ×
      </button>
    </div>
  );
}
