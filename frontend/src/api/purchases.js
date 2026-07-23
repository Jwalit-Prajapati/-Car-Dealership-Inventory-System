import { apiClient } from './client';

export function purchase({ vehicleId, quantity }) {
  return apiClient.post('/api/purchases', { vehicleId, quantity });
}
