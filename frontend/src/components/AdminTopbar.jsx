import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Bell, MessageSquare } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function AdminTopbar() {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();
  const { fullName, isAdmin } = useAuth();

  function handleSubmit(event) {
    event.preventDefault();
    const trimmed = query.trim();
    navigate(trimmed ? `/admin/vehicles?make=${encodeURIComponent(trimmed)}` : '/admin/vehicles');
  }

  return (
    <header className="flex items-center justify-between gap-4 border-b border-border-subtle bg-surface-1 px-6 py-4">
      <form onSubmit={handleSubmit} className="w-full max-w-sm">
        <div className="flex items-center gap-2 rounded-lg border border-border-subtle bg-surface-2 px-3 py-2">
          <Search className="h-4 w-4 text-text-muted" />
          <input
            type="text"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search vehicles by make..."
            className="w-full bg-transparent text-sm text-text-primary placeholder:text-text-muted focus:outline-none"
          />
        </div>
      </form>

      <div className="flex items-center gap-3">
        <button
          type="button"
          title="Coming soon"
          disabled
          className="rounded-full p-2 text-text-muted"
        >
          <MessageSquare className="h-5 w-5" />
        </button>
        <button
          type="button"
          title="Coming soon"
          disabled
          className="rounded-full p-2 text-text-muted"
        >
          <Bell className="h-5 w-5" />
        </button>

        <div className="ml-1 flex items-center gap-3 rounded-lg border border-border-subtle p-2">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-[var(--accent)] text-sm font-semibold text-white">
            {fullName ? fullName.charAt(0).toUpperCase() : '?'}
          </div>
          <div className="hidden min-w-0 sm:block">
            <p className="truncate text-sm font-medium text-text-primary">{fullName || 'Admin'}</p>
            <p className="text-xs text-text-muted">{isAdmin ? 'Admin' : 'User'}</p>
          </div>
        </div>
      </div>
    </header>
  );
}
