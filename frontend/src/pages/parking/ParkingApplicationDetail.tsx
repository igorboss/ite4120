import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Alert, Button, DatePicker, Descriptions, Form, Input, Select, Spin } from 'antd';
import { AppCard, AppTag } from '@helex/ui';
import type { Dayjs } from 'dayjs';
import { parkingApi, type Details, type Decision } from './parkingApi';

export const ParkingApplicationDetail = () => {
  const { id = '' } = useParams();
  const [record, setRecord] = useState<Details | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [revision, setRevision] = useState(0);
  useEffect(() => {
    let active = true;
    setLoading(true);
    setRecord(null);
    parkingApi.get(id).then(value => { if (active) setRecord(value); })
      .catch(e => { if (active) setError(e.message); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [id, revision]);
  const submit = async (action: () => Promise<void>, message: string) => {
    setSaving(true); setError(''); setSuccess('');
    try { await action(); setSuccess(message); setRevision(value => value + 1); }
    catch (e) { setError(e instanceof Error ? e.message : 'Request failed'); }
    finally { setSaving(false); }
  };
  const decide = (values: { date: Dayjs; type: Decision['decision_type']; reason?: string }) =>
    submit(() => parkingApi.decide(id, { decision_date: values.date.format('YYYY-MM-DD'), decision_type: values.type, decision_reason: values.reason || null }), 'Decision recorded.');
  const issue = (values: { from: Dayjs; until: Dayjs }) =>
    submit(() => parkingApi.issue(id, { valid_from: values.from.format('YYYY-MM-DD'), valid_until: values.until.format('YYYY-MM-DD') }), 'Parking permit issued.');
  return <AppCard title={`Parking application ${id}`}>
    <p><Link to="/parking/applications">Back to applications</Link></p>
    {error && <Alert type="error" message={error} showIcon />}
    {success && <Alert type="success" message={success} showIcon />}
    {loading ? <Spin /> : record && <div style={{ display: 'grid', gap: 24 }}>
      <Descriptions title="Application details" bordered column={1}>
        <Descriptions.Item label="Personal identity code">{record.personal_code}</Descriptions.Item>
        <Descriptions.Item label="Application date">{record.application_date}</Descriptions.Item>
        <Descriptions.Item label="Status"><AppTag>{record.status}</AppTag></Descriptions.Item>
        <Descriptions.Item label="Reason">{record.reason || 'Not provided'}</Descriptions.Item>
      </Descriptions>
      <section><h2>Decision</h2>
        {record.decision ? <Descriptions bordered column={1}>
          <Descriptions.Item label="Outcome"><AppTag>{record.decision.decision_type}</AppTag></Descriptions.Item>
          <Descriptions.Item label="Decision date">{record.decision.decision_date}</Descriptions.Item>
          <Descriptions.Item label="Decision reason">{record.decision.decision_reason || 'Not provided'}</Descriptions.Item>
        </Descriptions> : <Form layout="vertical" onFinish={decide} disabled={saving}>
          <Form.Item name="date" label="Decision date" rules={[{ required: true }]}><DatePicker /></Form.Item>
          <Form.Item name="type" label="Decision type" rules={[{ required: true }]}><Select options={[{ value: 'APPROVED', label: 'Approved' }, { value: 'REJECTED', label: 'Rejected' }]} /></Form.Item>
          <Form.Item name="reason" label="Decision reason (optional)" rules={[{ max: 500 }]}><Input.TextArea maxLength={500} showCount /></Form.Item>
          <Button type="primary" htmlType="submit" loading={saving}>Record decision</Button>
        </Form>}
      </section>
      <section><h2>Parking permit</h2>
        {record.permit ? <Descriptions bordered column={1}>
          <Descriptions.Item label="Permit number">{record.permit.permit_number}</Descriptions.Item>
          <Descriptions.Item label="Valid from">{record.permit.valid_from}</Descriptions.Item>
          <Descriptions.Item label="Valid until">{record.permit.valid_until}</Descriptions.Item>
          <Descriptions.Item label="Status"><AppTag>{record.permit.status}</AppTag></Descriptions.Item>
        </Descriptions> : record.decision?.decision_type !== 'APPROVED' ? <>
          <Alert type="warning" message="A parking permit cannot be issued without an approved decision." showIcon />
          <Button onClick={() => { setSuccess(''); setError('Cannot issue a parking permit without an approved decision.'); }}>Issue permit</Button>
        </> : <Form layout="vertical" onFinish={issue} disabled={saving}>
          <Form.Item name="from" label="Valid from" rules={[{ required: true }]}><DatePicker /></Form.Item>
          <Form.Item name="until" label="Valid until" dependencies={['from']} rules={[{ required: true }, ({ getFieldValue }) => ({ validator(_, value) { return value && getFieldValue('from') && value.isBefore(getFieldValue('from'), 'day') ? Promise.reject(new Error('Valid until must not precede valid from')) : Promise.resolve(); } })]}><DatePicker /></Form.Item>
          <Button type="primary" htmlType="submit" loading={saving}>Issue permit</Button>
        </Form>}
      </section>
    </div>}
  </AppCard>;
};
