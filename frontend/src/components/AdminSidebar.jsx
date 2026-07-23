import { NavLink } from 'react-router-dom';
import {
  Home,
  LayoutDashboard,
  BarChart3,
  Car,
  Users,
  Receipt,
  MessageSquare,
  HelpCircle,
  Settings,
} from 'lucide-react';

const NAV_ITEMS = [
  { label: 'Dashboard', to: '/admin', icon: LayoutDashboard, end: true },
  { label: 'Statistics', to: '/admin/stats', icon: BarChart3 },
  { label: 'Car Listing', to: '/admin/vehicles', icon: Car },
];

const COMING_SOON_ITEMS = [
  { label: 'Customers', icon: Users },
  { label: 'Transactions', icon: Receipt },
  { label: 'Messages', icon: MessageSquare },
  { label: 'Help Request', icon: HelpCircle },
  { label: 'Settings', icon: Settings },
];

function NavItem({ label, to, icon: Icon, end }) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) =>
        `flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
          isActive
            ? 'bg-[var(--accent)] text-white'
            : 'text-text-secondary hover:bg-surface-2 hover:text-text-primary'
        }`
      }
    >
      <Icon className="h-4 w-4" />
      {label}
    </NavLink>
  );
}

function ComingSoonItem({ label, icon: Icon }) {
  return (
    <div
      title="Coming soon"
      className="flex cursor-default items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-text-muted"
    >
      <Icon className="h-4 w-4" />
      {label}
      <span className="ml-auto rounded-full bg-surface-2 px-2 py-0.5 text-[10px] uppercase tracking-wide text-text-muted">
        Soon
      </span>
    </div>
  );
}

export default function AdminSidebar() {
  return (
    <aside className="flex h-full w-64 shrink-0 flex-col border-r border-border-subtle bg-surface-1 p-4">
      <NavLink to="/" className="px-2 py-2 text-lg font-semibold text-text-primary hover:text-[var(--accent)]">
        Vehicle Inventory
      </NavLink>

      <nav className="mt-4 flex flex-1 flex-col gap-1">
        <NavLink
          to="/"
          className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-text-secondary transition-colors hover:bg-surface-2 hover:text-text-primary"
        >
          <Home className="h-4 w-4" />
          Back to Site
        </NavLink>

        <div className="my-1 border-t border-border-subtle" />

        {NAV_ITEMS.map((item) => (
          <NavItem key={item.label} {...item} />
        ))}

        <div className="my-3 border-t border-border-subtle" />

        {COMING_SOON_ITEMS.map((item) => (
          <ComingSoonItem key={item.label} {...item} />
        ))}
      </nav>
    </aside>
  );
}
