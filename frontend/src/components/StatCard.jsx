export default function StatCard({ label, value, highlight = false, icon: Icon }) {
  return (
    <div
      className={`rounded-lg border p-4 shadow-sm ${
        highlight
          ? 'border-[var(--accent)] bg-[var(--accent)] text-white'
          : 'border-border-subtle bg-surface-1'
      }`}
    >
      <div className="flex items-center justify-between">
        <p className={`text-sm font-medium ${highlight ? 'text-white/80' : 'text-text-muted'}`}>{label}</p>
        {Icon && <Icon className={`h-4 w-4 ${highlight ? 'text-white/80' : 'text-text-muted'}`} />}
      </div>
      <p className={`mt-2 text-2xl font-bold ${highlight ? 'text-white' : 'text-text-primary'}`}>{value}</p>
    </div>
  );
}
