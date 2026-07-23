export default function StatCard({ label, value, highlight = false }) {
  return (
    <div
      className={`rounded-lg border p-4 shadow-sm ${
        highlight ? 'border-slate-900 bg-slate-900 text-white' : 'border-slate-200 bg-white'
      }`}
    >
      <p className={`text-sm font-medium ${highlight ? 'text-slate-300' : 'text-slate-500'}`}>{label}</p>
      <p className={`mt-2 text-2xl font-bold ${highlight ? 'text-white' : 'text-slate-900'}`}>{value}</p>
    </div>
  );
}
