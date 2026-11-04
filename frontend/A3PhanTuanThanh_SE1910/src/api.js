// Author: Phan Tuan Thanh - CE190595
import axios from 'axios';
import { getSession, clearSession } from './session.js';

export const api = axios.create({ baseURL: '/api', timeout: 10000 });
api.interceptors.request.use((config) => {
  const session = getSession();
  if (session) config.headers.Authorization = `Bearer ${session.token}`;
  return config;
});
api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401 && getSession()) {
    clearSession();
    window.dispatchEvent(new Event('session-expired'));
  }
  return Promise.reject(error);
});
export function errorMessage(error) {
  const body = error.response?.data;
  const fields = Object.entries(body?.fieldErrors || {}).map(([name, message]) => `${name.split('.').pop()}: ${message}`);
  return [body?.message || 'Unable to complete the request. Please try again.', ...fields].join(' ');
}
export const money = (value) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);
