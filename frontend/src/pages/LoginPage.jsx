import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import { ApiError } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/ToastProvider';

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
    <div className="flex justify-center p-6">
      <form onSubmit={handleSubmit} noValidate className="w-full max-w-sm space-y-4">
        <h1 className="text-2xl font-semibold">Login</h1>

        <div>
          <label htmlFor="email" className="block text-sm font-medium text-slate-700">
            Email
          </label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:border-slate-500 focus:outline-none"
          />
          {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email}</p>}
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-slate-700">
            Password
          </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:border-slate-500 focus:outline-none"
          />
          {errors.password && <p className="mt-1 text-sm text-red-600">{errors.password}</p>}
        </div>

        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800 disabled:opacity-50"
        >
          {submitting ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
}
