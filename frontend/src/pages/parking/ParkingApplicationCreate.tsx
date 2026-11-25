
import { AppCard, AppButtonPrimary, useAppNotification } from '@helex/ui';
import { Form, Input, DatePicker } from 'antd';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import type { Dayjs } from 'dayjs';

export const ParkingApplicationCreate = () => {
  const notify = useAppNotification();
  const navigate = useNavigate();
  const [saving, setSaving] = useState(false);

  const submit = async (values: {
    personalCode: string;
    applicationDate: Dayjs;
    reason?: string;
  }) => {
    setSaving(true);

    try {
      const user = localStorage.getItem('helex_mock_user') || 'superadmin';

      const response = await fetch('/api/v1/permit-applications', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          personal_code: values.personalCode,
          application_date: values.applicationDate.format('YYYY-MM-DD'),
          reason: values.reason || null,
        }),
      });

      if (!response.ok) {
        const problem = await response.json().catch(() => null);
        throw new Error(problem?.detail || `HTTP ${response.status}`);
      }

      notify.success(
        'Application created',
        'The parking permit application was saved.'
      );
      navigate('/parking/applications');
    } catch (error) {
      notify.error(
        'Could not create application',
        error instanceof Error ? error.message : 'An unexpected error occurred'
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <AppCard title="Create a parking permit application">
      <p><Link to="/parking/applications">Back to applications</Link></p><p>Enter the applicant's details to create a new application.</p>

      <Form layout="vertical" onFinish={submit}>
        <Form.Item
          name="personalCode"
          label="Personal identity code"
          rules={[
            {
              required: true,
              message: 'Please enter a personal identity code',
            },
            {
              pattern: /^\d{11}$/,
              message: 'The personal identity code must contain exactly 11 digits',
            },
          ]}
        >
          <Input placeholder="Enter the applicant's 11-digit personal code" />
        </Form.Item>

        <Form.Item
          name="applicationDate"
          label="Application date"
          rules={[
            {
              required: true,
              message: 'Please select an application date',
            },
          ]}
        >
          <DatePicker
            style={{ width: '100%' }}
            format="YYYY-MM-DD"
          />
        </Form.Item>

        <Form.Item
          name="reason"
          label="Reason (optional)"
          rules={[
            {
              max: 500,
              message: 'Reason must not exceed 500 characters',
            },
          ]}
        >
          <Input.TextArea
            rows={4}
            maxLength={500}
            showCount
            placeholder="Enter the reason for the application (optional)"
          />
        </Form.Item>

        <Form.Item>
          <AppButtonPrimary htmlType="submit" loading={saving}>
            Create application
          </AppButtonPrimary>
        </Form.Item>
      </Form>
    </AppCard>
  );
};