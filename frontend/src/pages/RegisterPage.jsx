import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../api/auth';
import { ApiError } from '../api/client';
import { useToast } from '../components/ToastProvider';
import Spinner from '../components/Spinner';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function RegisterPage() {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();
  const { showError } = useToast();

  function validate() {
    const nextErrors = {};
    if (!firstName.trim()) nextErrors.firstName = 'First name is required.';
    if (!lastName.trim()) nextErrors.lastName = 'Last name is required.';
    if (!email.trim()) {
      nextErrors.email = 'Email is required.';
    } else if (!EMAIL_PATTERN.test(email.trim())) {
      nextErrors.email = 'Enter a valid email address.';
    }
    if (password.length < 8) nextErrors.password = 'Password must be at least 8 characters.';
    return nextErrors;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setSubmitting(true);
    try {
      await register({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        email: email.trim(),
        password,
      });
      navigate('/login', { state: { message: 'Registration successful. Please log in.' } });
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Unable to register. Please try again.';
      showError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex justify-center p-6">
      <form onSubmit={handleSubmit} noValidate className="w-full max-w-sm space-y-4">
        <h1 className="text-2xl font-semibold">Register</h1>

        <div>
          <label htmlFor="firstName" className="block text-sm font-medium text-slate-700">
            First name
          </label>
          <input
            id="firstName"
            type="text"
            value={firstName}
            onChange={(event) => setFirstName(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:border-slate-500 focus:outline-none"
          />
          {errors.firstName && <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>}
        </div>

        <div>
          <label htmlFor="lastName" className="block text-sm font-medium text-slate-700">
            Last name
          </label>
          <input
            id="lastName"
            type="text"
            value={lastName}
            onChange={(event) => setLastName(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:border-slate-500 focus:outline-none"
          />
          {errors.lastName && <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>}
        </div>

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
          className="flex w-full items-center justify-center gap-2 rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800 disabled:opacity-50"
        >
          {submitting && <Spinner />}
          {submitting ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
}
