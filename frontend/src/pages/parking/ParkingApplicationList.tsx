import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Alert, Button, DatePicker, Form, Select, Space, Table } from 'antd';
import { AppCard, AppTag } from '@helex/ui';
import type { Dayjs } from 'dayjs';
import { parkingApi, type Application } from './parkingApi';

type Filters = { status?: string; from?: Dayjs; to?: Dayjs; sort?: string };
export const ParkingApplicationList = () => {
  const [filters, setFilters] = useState<Filters>({});
  const [page, setPage] = useState(1);
  const [rows, setRows] = useState<Application[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    const query = new URLSearchParams({ limit: '10', offset: String((page - 1) * 10) });
    if (filters.status) query.set('status', filters.status);
    if (filters.from) query.set('applicationDateFrom', filters.from.format('YYYY-MM-DD'));
    if (filters.to) query.set('applicationDateTo', filters.to.format('YYYY-MM-DD'));
    if (filters.sort) query.set('sort', filters.sort);
    parkingApi.list(query).then(result => {
      if (active) { setRows(result.data); setTotal(result.meta.total); }
    }).catch(e => { if (active) { setError(e.message); setRows([]); setTotal(0); } })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [filters, page]);
  return <AppCard title="Accessible parking permit register">
    <p><Link to="/parking/applications/new">Create application</Link> | <Link to="/animals">Animals register</Link></p>
    <Form layout="vertical" onFinish={(values: Filters) => { setPage(1); setFilters(values); }}>
      <Space wrap align="end">
        <Form.Item name="status" label="Status"><Select allowClear style={{ width: 150 }} options={['RECEIVED','DECIDED'].map(value => ({ value, label: value }))} /></Form.Item>
        <Form.Item name="from" label="Application date from"><DatePicker /></Form.Item>
        <Form.Item name="to" label="Application date to" dependencies={['from']} rules={[({ getFieldValue }) => ({ validator(_, value) { return value && getFieldValue('from') && value.isBefore(getFieldValue('from'), 'day') ? Promise.reject(new Error('End date must not precede start date')) : Promise.resolve(); } })]}><DatePicker /></Form.Item>
        <Form.Item name="sort" label="Sort"><Select allowClear style={{ width: 180 }} options={[{value:'-applicationDate',label:'Newest first'},{value:'applicationDate',label:'Oldest first'},{value:'status',label:'Status A-Z'},{value:'-status',label:'Status Z-A'}]} /></Form.Item>
        <Form.Item><Button htmlType="submit" type="primary">Apply filters</Button></Form.Item>
      </Space>
    </Form>
    {error && <Alert type="error" message={error} showIcon />}
    <Table<Application> rowKey="id" loading={loading} dataSource={rows} scroll={{ x: 500 }}
      pagination={{ current: page, pageSize: 10, total, showSizeChanger: false, onChange: setPage }}
      columns={[
        { title: 'Application ID', dataIndex: 'id', render: (id: number) => <Link to={`/parking/applications/${id}`}>{id}</Link> },
        { title: 'Personal identity code', dataIndex: 'personal_code' },
        { title: 'Application date', dataIndex: 'application_date' },
        { title: 'Status', dataIndex: 'status', render: (status: string) => <AppTag>{status}</AppTag> },
      ]} />
  </AppCard>;
};
