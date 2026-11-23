import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppButtonPrimary, ResourceList, useAppNotification, type ResourceListColumn } from '@helex/ui';
import { PlusOutlined } from '@ant-design/icons';
import { Card, Select, Space, Tag, Typography } from 'antd';
import { lostFoundApi, type CollectionPoint, type ItemReport, type ReferenceData } from './api';

export const LostFoundList = () => {
  const navigate = useNavigate(); const notify = useAppNotification();
  const [reports,setReports] = useState<ItemReport[]>([]); const [categories,setCategories] = useState<ReferenceData[]>([]);
  const [locations,setLocations] = useState<ReferenceData[]>([]); const [points,setPoints] = useState<CollectionPoint[]>([]);
  const [search,setSearch] = useState(''); const [type,setType] = useState<string>(); const [category,setCategory] = useState<string>(); const [location,setLocation] = useState<string>();
  const [loading,setLoading] = useState(true); const [registryError,setRegistryError] = useState('');
  const load = useCallback(() => { setLoading(true); lostFoundApi.list({textContains:search,reportType:type,categoryCode:category,locationCode:location})
    .then(r=>setReports(r.data)).catch(e=>notify.error('Could not load reports',e.message)).finally(()=>setLoading(false)); },[search,type,category,location,notify]);
  useEffect(()=>{ lostFoundApi.categories().then(setCategories).catch(()=>{}); lostFoundApi.locations().then(setLocations).catch(()=>{});
    lostFoundApi.collectionPoints().then(setPoints).catch(e=>setRegistryError(e.message)); },[]);
  useEffect(()=>{ const t=setTimeout(load,250); return ()=>clearTimeout(t); },[load]);
  const columns: ResourceListColumn<ItemReport>[] = [
    {key:'reportType',title:'Type',dataIndex:'reportType',render:(v:string)=><Tag color={v==='LOST'?'volcano':'green'}>{v}</Tag>},
    {key:'itemName',title:'Item',dataIndex:'itemName'}, {key:'categoryName',title:'Category',dataIndex:'categoryName'},
    {key:'reportDate',title:'Date',dataIndex:'reportDate'}, {key:'locationName',title:'Location',dataIndex:'locationName'},
    {key:'description',title:'Description',dataIndex:'description',defaultVisible:false},
  ];
  return <div style={{display:'grid',gap:20}}>
    <ResourceList<ItemReport> title="Lost and Found Registry" columns={columns} dataSource={reports} rowKey="id" loading={loading}
      search={{value:search,onChange:setSearch,placeholder:'Search item name or description…'}}
      filter={{content:<Space wrap><Select allowClear placeholder="Report type" style={{minWidth:150}} value={type} onChange={setType} options={[{value:'LOST',label:'Lost'},{value:'FOUND',label:'Found'}]}/><Select allowClear placeholder="Category" style={{minWidth:160}} value={category} onChange={setCategory} options={categories.map(c=>({value:c.code,label:c.name}))}/><Select allowClear placeholder="Location" style={{minWidth:180}} value={location} onChange={setLocation} options={locations.map(l=>({value:l.code,label:l.name}))}/></Space>,onApply:load,onClear:()=>{setType(undefined);setCategory(undefined);setLocation(undefined);}}}
      actions={<AppButtonPrimary icon={<PlusOutlined/>} onClick={()=>navigate('/lost-found/new')}>Create report</AppButtonPrimary>}/>
    <Card title="Municipal collection points" size="small">
      {registryError ? <Typography.Text type="warning">External registry unavailable: {registryError}. Reports and search remain available.</Typography.Text> :
        <Space direction="vertical" style={{width:'100%'}}>{points.map(p=><div key={p.id}><b>{p.name}</b><div>{p.address}</div></div>)}</Space>}
    </Card>
  </div>;
};
