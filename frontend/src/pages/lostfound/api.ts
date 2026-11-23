export interface ReferenceData { code: string; name: string; address?: string }
export interface ItemReport {
  id: number; reportType: 'LOST' | 'FOUND'; itemName: string; description: string;
  reportDate: string; categoryCode: string; categoryName: string;
  locationCode: string; locationName: string; locationAddress: string;
}
export interface CollectionPoint { id: string; name: string; address: string }
export interface QueryResult<T> { data: T[]; meta?: { total?: number } }
const headers = () => ({ Authorization: `Bearer ${localStorage.getItem('helex_mock_user') || 'superadmin'}` });
async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) { const problem = await response.json().catch(() => null); throw new Error(problem?.detail || problem?.message || `HTTP ${response.status}`); }
  return response.json();
}
export const lostFoundApi = {
  list: (filters: { textContains?: string; reportType?: string; categoryCode?: string; locationCode?: string }): Promise<QueryResult<ItemReport>> => {
    const q = new URLSearchParams({ limit: '100' }); Object.entries(filters).forEach(([k,v]) => { if (v) q.set(k,v); });
    return fetch(`/api/lost-found/reports?${q}`, { headers: headers() }).then(r => handle<QueryResult<ItemReport>>(r));
  },
  create: (body: { reportType: 'LOST'|'FOUND'; itemName: string; description: string; reportDate: string; categoryCode: string; locationCode: string }) => fetch('/api/lost-found/reports', { method:'POST', headers:{...headers(),'Content-Type':'application/json'}, body:JSON.stringify(body) }).then(r=>handle<ItemReport>(r)),
  categories: () => fetch('/api/lost-found/categories',{headers:headers()}).then(r=>handle<ReferenceData[]>(r)),
  locations: () => fetch('/api/lost-found/locations',{headers:headers()}).then(r=>handle<ReferenceData[]>(r)),
  collectionPoints: () => fetch('/api/lost-found/collection-points',{headers:headers()}).then(r=>handle<CollectionPoint[]>(r)),
};
