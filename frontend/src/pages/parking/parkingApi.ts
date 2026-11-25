export interface Application {
  id: number;
  personal_code: string;
  application_date: string;
  status: string;
  reason?: string | null;
}
export interface Decision {
  decision_date: string;
  decision_type: 'APPROVED' | 'REJECTED';
  decision_reason?: string | null;
}
export interface Permit {
  id: number;
  permit_number: string;
  valid_from: string;
  valid_until: string;
  status: string;
}
export interface Details extends Application {
  decision?: Decision | null;
  permit?: Permit | null;
}
const base = '/api/v1/permit-applications';
async function request(path: string, body?: object): Promise<Response> {
  const response = await fetch(path, {
    method: body ? 'POST' : 'GET',
    headers: {
      Authorization: `Bearer ${localStorage.getItem('helex_mock_user') || 'superadmin'}`,
      'Content-Type': 'application/json',
    },
    ...(body ? { body: JSON.stringify(body) } : {}),
  });
  if (!response.ok) {
    const problem = await response.json().catch(() => null);
    throw new Error(problem?.detail || `Request failed (HTTP ${response.status})`);
  }
  return response;
}
export const parkingApi = {
  list: async (query: URLSearchParams): Promise<{ data: Application[]; meta: { total: number } }> =>
    (await request(`${base}?${query}`)).json(),
  get: async (id: string): Promise<Details> => (await request(`${base}/${id}`)).json(),
  decide: async (id: string, body: Decision) => { await request(`${base}/${id}/decision`, body); },
  issue: async (id: string, body: { valid_from: string; valid_until: string }) => {
    await request(`${base}/${id}/permits`, body);
  },
};
