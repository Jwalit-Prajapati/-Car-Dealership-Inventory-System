import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import { ApiError } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/ToastProvider';
import Spinner from '../components/Spinner';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const auth = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { showSuccess, showError } = useToast();
  const redirectMessageShown = useRef(false);

  useEffect(() => {
    if (location.state?.message && !redirectMessageShown.current) {
      redirectMessageShown.current = true;
      showSuccess(location.state.message);
      navigate(location.pathname, { replace: true, state: null });
    }
    // Runs once on mount to consume the redirect message; re-running on every
    // location/showSuccess identity change would re-trigger the toast + navigate loop.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function validate() {
    const nextErrors = {};
    if (!email.trim()) nextErrors.email = 'Email is required.';
    if (!password) nextErrors.password = 'Password is required.';
    return nextErrors;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setSubmitting(true);
    try {
      const data = await login({ email: email.trim(), password });
      auth.login(data);
      navigate('/');
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Unable to log in. Please try again.';
      showError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex justify-center px-6 py-16">
      <form
        onSubmit={handleSubmit}
        noValidate
        className="w-full max-w-sm space-y-4 rounded-lg border border-border-subtle bg-surface-1 p-8 shadow-sm"
      >
        <h1 className="text-2xl font-semibold text-text-primary">Login</h1>

        <div>
          <label htmlFor="email" className="block text-sm font-medium text-text-secondary">
            Email
          </label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className="mt-1 w-full rounded border border-border-subtle bg-surface-2 px-3 py-2 text-text-primary focus:border-[var(--accent)] focus:outline-none"
          />
          {errors.email && <p className="mt-1 text-sm text-red-400">{errors.email}</p>}
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-text-secondary">
            Password
          </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="mt-1 w-full rounded border border-border-subtle bg-surface-2 px-3 py-2 text-text-primary focus:border-[var(--accent)] focus:outline-none"
          />
          {errors.password && <p className="mt-1 text-sm text-red-400">{errors.password}</p>}
        </div>

        <button
          type="submit"
          disabled={submitting}
          className="flex w-full items-center justify-center gap-2 rounded bg-[var(--accent)] px-4 py-2 text-white hover:bg-[var(--accent-hover)] disabled:opacity-50"
        >
          {submitting && <Spinner />}
          {submitting ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
}
