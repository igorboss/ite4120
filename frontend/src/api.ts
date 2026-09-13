// The animals API client — thin fetch wrappers over the backend the group owns.
// The Vite dev proxy forwards /api to the backend, so paths are same-origin.
// The Authorization header is set by MockAuthProvider's session (dev).

export interface Animal {
  id: number;
  registryCode: string;
  name: string;
  speciesCode: string;
  birthDate?: string;
  ownerIsikukood?: string;
  chipNumber?: string;
}

export interface OwnerInfo {
  isikukood: string;
  firstName: string;
  lastName: string;
  address: string;
}

export interface QueryResult<T> {
  data: T[];
  meta?: { total?: number };
}

function authHeaders(): Record<string, string> {
  // MockAuthProvider persists the signed-in mock user here.
  const user = localStorage.getItem('helex_mock_user') || 'superadmin';
  return { Authorization: `Bearer ${user}` };
}

async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) {
    // The backend answers application/problem+json — surface its human text.
    const problem = await response.json().catch(() => null);
    throw new Error(problem?.detail || `HTTP ${response.status}`);
  }
  return response.json();
}

export const animalsApi = {
  list: (textContains?: string): Promise<QueryResult<Animal>> =>
    fetch(
      '/api/animals?limit=50' +
        (textContains ? `&textContains=${encodeURIComponent(textContains)}` : ''),
      { headers: authHeaders() },
    ).then((r) => handle<QueryResult<Animal>>(r)),

  create: (body: Omit<Animal, 'id'>): Promise<Animal> =>
    fetch('/api/animals', {
      method: 'POST',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Animal>(r)),

  owner: (id: number): Promise<OwnerInfo> =>
    fetch(`/api/animals/${id}/owner`, { headers: authHeaders() }).then((r) =>
      handle<OwnerInfo>(r),
    ),

  species: (): Promise<{ code: string; name: string }[]> =>
    fetch('/api/animals/species', { headers: authHeaders() }).then((r) =>
      handle<{ code: string; name: string }[]>(r),
    ),
};
