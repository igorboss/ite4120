import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Select } from 'antd';
import { AppButtonPrimary, AppButtonSecondary, AppCard, useAppNotification } from '@helex/ui';
import { booksApi } from '../api';

/**
 * Registration form. Field-by-field this follows the UI specification in
 * docs/specifications/BOOKS.01-book-register.md.
 * Every input is implemented using library components.
 */
export const BookCreate = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [saving, setSaving] = useState(false);

  
  const submit = (values: {
    isbn: string;
    title: string;
    genre: string;
    author: string;
    pageCount: number;
    publicationYear: number;
  }) => {
    setSaving(true);
    booksApi
      .create({
        isbn: values.isbn,
        title: values.title,
        genre: values.genre,
        author: values.author,
        pageCount: values.pageCount,
        publicationYear: values.publicationYear,
      })
      .then((created) => {
        notify.success('Registered', `${created.title} · ${created.isbn}`);
        navigate('/books');
      })
      .catch((e) => notify.error('Registration failed', e.message)) // 400/409 problem detail, verbatim
      .finally(() => setSaving(false));
  };

  return (
    <AppCard title="Register a new book" style={{ maxWidth: 640, margin: '24px auto' }}>
      <Form layout="vertical" onFinish={submit} requiredMark>
        <Form.Item
          name="isbn"
          label="ISBN"
          rules={[{ required: true, max: 50 }]}
          extra="ISBN must contain 10 or 13 digits"
        >
          <Input placeholder="9780123456789" />
        </Form.Item>
        <Form.Item name="title" label="Title" rules={[{ required: true, max: 255 }]}>
          <Input placeholder="Book title" />
        </Form.Item>
        <Form.Item name="genre" label="Genre" rules={[{ required: true }]}>
<Select
 options={[
   { value: 'FICTION', label: 'Fiction' },
   { value: 'NON_FICTION', label: 'Non-fiction' },
   { value: 'FANTASY', label: 'Fantasy' },
   { value: 'SCIENCE_FICTION', label: 'Science Fiction' },
   { value: 'MYSTERY', label: 'Mystery' },
   { value: 'BIOGRAPHY', label: 'Biography' },
   { value: 'HISTORY', label: 'History' },
   { value: 'CHILDREN', label: 'Children' },
   { value: 'POETRY', label: 'Poetry' },
   { value: 'OTHER', label: 'Other' }
 ]}
 placeholder="Choose a genre"
/>
</Form.Item>
        <Form.Item name="author" label="Author" rules={[{ required: true }]}>
 <Input />
</Form.Item>

<Form.Item
 name="pageCount"
 label="Page count"
 rules={[{ required: true }]}
>
 <Input type="number" />
</Form.Item>

<Form.Item
 name="publicationYear"
 label="Publication year"
 rules={[{ required: true }]}
>
 <Input type="number" />
</Form.Item>
        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <AppButtonSecondary onClick={() => navigate('/books')}>Cancel</AppButtonSecondary>
          <AppButtonPrimary htmlType="submit" loading={saving}>
            Register
          </AppButtonPrimary>
        </div>
      </Form>
    </AppCard>
  );
};
