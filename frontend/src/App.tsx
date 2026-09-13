import { Navigate, Route, Routes } from 'react-router-dom';
import { AnimalList } from './pages/AnimalList';
import { AnimalCreate } from './pages/AnimalCreate';

export const App = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/animals" replace />} />
    <Route path="/animals" element={<AnimalList />} />
    <Route path="/animals/new" element={<AnimalCreate />} />
  </Routes>
);
