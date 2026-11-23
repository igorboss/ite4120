import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppButtonPrimary, AppButtonSecondary, AppCard, useAppNotification } from '@helex/ui';
import { DatePicker, Form, Input, Select } from 'antd';
import type { Dayjs } from 'dayjs';
import { lostFoundApi, type ReferenceData } from './api';

export const LostFoundCreate = () => {
 const navigate=useNavigate(); const notify=useAppNotification(); const [categories,setCategories]=useState<ReferenceData[]>([]); const [locations,setLocations]=useState<ReferenceData[]>([]); const [saving,setSaving]=useState(false);
 useEffect(()=>{lostFoundApi.categories().then(setCategories).catch(e=>notify.error('Could not load categories',e.message));lostFoundApi.locations().then(setLocations).catch(e=>notify.error('Could not load locations',e.message));},[notify]);
 const submit=(v:{reportType:'LOST'|'FOUND';itemName:string;description:string;reportDate:Dayjs;categoryCode:string;locationCode:string})=>{setSaving(true);lostFoundApi.create({...v,reportDate:v.reportDate.format('YYYY-MM-DD')}).then(()=>{notify.success('Report created','Your report has been added to the registry.');navigate('/lost-found');}).catch(e=>notify.error('Could not create report',e.message)).finally(()=>setSaving(false));};
 return <AppCard title="Create a lost or found report" style={{maxWidth:680,margin:'24px auto'}}><Form layout="vertical" onFinish={submit} requiredMark>
  <Form.Item name="reportType" label="Report type" rules={[{required:true}]}><Select options={[{value:'LOST',label:'I lost an item'},{value:'FOUND',label:'I found an item'}]}/></Form.Item>
  <Form.Item name="itemName" label="Item name" rules={[{required:true,max:160}]}><Input placeholder="e.g. Black leather wallet"/></Form.Item>
  <Form.Item name="description" label="Description" rules={[{required:true,max:2000}]}><Input.TextArea rows={4} placeholder="Describe the item and any distinguishing features"/></Form.Item>
  <Form.Item name="reportDate" label="Date of loss or discovery" rules={[{required:true}]}><DatePicker style={{width:'100%'}} disabledDate={d=>!!d&&d.isAfter(new Date(),'day')}/></Form.Item>
  <Form.Item name="categoryCode" label="Category" rules={[{required:true}]}><Select options={categories.map(c=>({value:c.code,label:c.name}))}/></Form.Item>
  <Form.Item name="locationCode" label="Location in Parma" rules={[{required:true}]}><Select showSearch optionFilterProp="label" options={locations.map(l=>({value:l.code,label:l.name}))}/></Form.Item>
  <div style={{display:'flex',gap:8,justifyContent:'flex-end'}}><AppButtonSecondary onClick={()=>navigate('/lost-found')}>Cancel</AppButtonSecondary><AppButtonPrimary htmlType="submit" loading={saving}>Submit report</AppButtonPrimary></div>
 </Form></AppCard>;
};
