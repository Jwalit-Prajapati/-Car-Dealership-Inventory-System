import { apiClient } from './client';

export function list({ page, size, sort } = {}) {
  return apiClient.get('/api/vehicles', { page, size, sort });
}

export function search({ make, model, category, minPrice, maxPrice, page, size } = {}) {
  return apiClient.get('/api/vehicles/search', { make, model, category, minPrice, maxPrice, page, size });
}

export function create(vehicle) {
  return apiClient.post('/api/vehicles', vehicle);
}

export function update(id, vehicle) {
  return apiClient.put(`/api/vehicles/${id}`, vehicle);
}

export function remove(id) {
  return apiClient.delete(`/api/vehicles/${id}`);
}

export function restock(id, quantity) {
  return apiClient.post(`/api/vehicles/${id}/restock`, { quantity });
}
