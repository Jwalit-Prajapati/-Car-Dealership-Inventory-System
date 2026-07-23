import { apiClient } from './client';

export function login({ email, password }) {
  return apiClient.post('/api/auth/login', { email, password });
}

export function register({ firstName, lastName, email, password }) {
  return apiClient.post('/api/auth/register', { firstName, lastName, email, password });
}
