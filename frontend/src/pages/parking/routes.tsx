import { Route } from 'react-router-dom';
import { ParkingApplicationList } from './ParkingApplicationList';
import { ParkingApplicationDetail } from './ParkingApplicationDetail';
export const routes = [
  <Route key="parking-list" path="/parking/applications" element={<ParkingApplicationList />} />,
  <Route key="parking-detail" path="/parking/applications/:id" element={<ParkingApplicationDetail />} />,
];
