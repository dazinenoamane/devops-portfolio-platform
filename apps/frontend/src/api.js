const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(error.message || 'Request failed');
  }

  if (response.status === 204) return null;
  return response.json();
}

function authHeaders(token) {
  return { Authorization: `Bearer ${token}` };
}

export const api = {
  portfolio: () => request('/api/portfolio'),
  projects: () => request('/api/projects'),
  comment: (payload) => request('/api/comments', { method: 'POST', body: JSON.stringify(payload) }),
  login: (payload) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
  adminProfile: (token) => request('/api/admin/profile', { headers: authHeaders(token) }),
  updateProfile: (token, payload) => request('/api/admin/profile', {
    method: 'PUT',
    headers: authHeaders(token),
    body: JSON.stringify(payload),
  }),
  createProject: (token, payload) => request('/api/admin/projects', {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify(payload),
  }),
  updateProject: (token, id, payload) => request(`/api/admin/projects/${id}`, {
    method: 'PUT',
    headers: authHeaders(token),
    body: JSON.stringify(payload),
  }),
  deleteProject: (token, id) => request(`/api/admin/projects/${id}`, {
    method: 'DELETE',
    headers: authHeaders(token),
  }),
  adminComments: (token) => request('/api/admin/comments', { headers: authHeaders(token) }),
  moderate: (token, id, action) => request(`/api/admin/comments/${id}/${action}`, {
    method: 'PATCH',
    headers: authHeaders(token),
  }),
};
