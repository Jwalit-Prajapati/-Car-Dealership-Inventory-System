import { apiClient } from './client';

export function inventoryStats() {
  return apiClient.get('/api/admin/stats/inventory');
}

export function salesStats() {
  return apiClient.get('/api/admin/stats/sales');
}
