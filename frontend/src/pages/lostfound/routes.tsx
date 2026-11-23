import type { ReactElement } from 'react';
import { Route } from 'react-router-dom';
import { LostFoundList } from './LostFoundList';
import { LostFoundCreate } from './LostFoundCreate';
export const routes: ReactElement[] = [
  <Route key="lost-found-list" path="/lost-found" element={<LostFoundList/>}/> ,
  <Route key="lost-found-create" path="/lost-found/new" element={<LostFoundCreate/>}/> ,
];
