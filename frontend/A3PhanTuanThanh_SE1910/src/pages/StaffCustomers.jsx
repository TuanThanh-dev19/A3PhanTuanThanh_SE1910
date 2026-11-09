// Author: Phan Tuan Thanh - CE190595
import { useEffect, useRef, useState } from 'react';
import { api, errorMessage } from '../api.js';
import ProfileFields, { emptyProfile } from '../components/ProfileFields.jsx';
import Notice from '../components/Notice.jsx';
import Modal from '../components/Modal.jsx';

export default function StaffCustomers() {
  const [rows, setRows] = useState([]);
  const [search, setSearch] = useState('');
  const [editing, setEditing] = useState(null);
  const [editorOpen, setEditorOpen] = useState(false);
  const [profile, setProfile] = useState(emptyProfile);
  const [status, setStatus] = useState(1);
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);
  const [removing, setRemoving] = useState(null);
  const latestSearch = useRef(0);

  async function load(term = '') {
    const requestId = ++latestSearch.current;
    try {
      const { data } = await api.get('/staff/customers', { params: { search: term } });
      if (requestId === latestSearch.current) setRows(data);
    } catch (failure) {
      if (requestId === latestSearch.current) setError(errorMessage(failure));
    }
  }

  useEffect(() => {
    load();
  }, []);

  function edit(customer) {
    setRemoving(null);
    setEditorOpen(true);
    setEditing(customer);
    setProfile(
      customer
        ? {
            customerFullName: customer.customerFullName || '',
            telephone: customer.telephone || '',
            emailAddress: customer.emailAddress,
            customerBirthday: customer.customerBirthday || '',
          }
        : emptyProfile,
    );
    setStatus(customer?.customerStatus || 1);
    setPassword('');
    setError('');
    setMessage('');
  }

  function closeEditor() {
    setEditorOpen(false);
    setEditing(null);
    setProfile({ ...emptyProfile });
    setPassword('');
    setError('');
  }

  function confirmRemoval(customer) {
    setEditorOpen(false);
    setRemoving(customer);
    setError('');
    setMessage('');
  }

  function closeRemoval() {
    setRemoving(null);
    setError('');
  }

  async function save(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    setBusy(true);
    const body = { profile, customerStatus: Number(status), ...(password ? { password } : {}) };
    try {
      if (editing) await api.put(`/staff/customers/${editing.customerId}`, body);
      else await api.post('/staff/customers', body);
      const wasEdit = Boolean(editing);
      closeEditor();
      await load(search);
      setMessage(wasEdit ? 'Customer updated.' : 'Customer created.');
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    setError('');
    setBusy(true);
    try {
      await api.delete(`/staff/customers/${removing.customerId}`);
      setMessage('Customer deleted.');
      setRemoving(null);
      await load(search);
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section>
      <div className="catalog-heading">
        <h1>Manage customers</h1>
        <button disabled={busy} onClick={() => edit(null)}>
          Add customer
        </button>
      </div>
      <Notice error={!editorOpen && !removing ? error : ''} message={message} />
      <form
        className="search"
        onSubmit={(event) => {
          event.preventDefault();
          load(search);
        }}
      >
        <label>
          Search customers
          <input
            maxLength={100}
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Name or email"
          />
        </label>
        <button>Search</button>
      </form>
      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Telephone</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((customer) => (
              <tr key={customer.customerId}>
                <td>{customer.customerFullName}</td>
                <td>{customer.emailAddress}</td>
                <td>{customer.telephone}</td>
                <td>{customer.customerStatus === 1 ? 'Active' : 'Inactive'}</td>
                <td className="actions">
                  <button className="secondary" disabled={busy} onClick={() => edit(customer)}>
                    Edit
                  </button>
                  <button
                    className="danger"
                    disabled={busy}
                    onClick={() => confirmRemoval(customer)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {rows.length === 0 && <p>No matching customers.</p>}
      {removing && (
        <Modal title="Delete customer" compact busy={busy} onClose={closeRemoval}>
          <Notice error={error} />
          <p>
            Delete {removing.customerFullName}? Customers with booking history must be deactivated
            instead.
          </p>
          <div className="actions">
            <button className="danger" disabled={busy} onClick={remove}>
              Confirm customer deletion
            </button>
            <button className="secondary" data-modal-focus disabled={busy} onClick={closeRemoval}>
              Keep customer
            </button>
          </div>
        </Modal>
      )}
      {editorOpen && (
        <Modal title={editing ? 'Edit customer' : 'Add customer'} busy={busy} onClose={closeEditor}>
          <Notice error={error} />
          <form onSubmit={save}>
            <ProfileFields value={profile} onChange={setProfile} disabled={busy} />
            <div className="form-grid">
              <label>
                Customer status
                <select
                  aria-label="Customer status"
                  disabled={busy}
                  value={status}
                  onChange={(event) => setStatus(event.target.value)}
                >
                  <option value={1}>Active</option>
                  <option value={2}>Inactive</option>
                </select>
              </label>
              <label>
                {editing ? 'New password (optional)' : 'Password'}
                <input
                  type="password"
                  disabled={busy}
                  required={!editing}
                  minLength={8}
                  maxLength={64}
                  autoComplete="new-password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
              </label>
            </div>
            <div className="actions">
              <button disabled={busy}>
                {busy ? 'Saving...' : editing ? 'Save customer' : 'Add customer'}
              </button>
              <button type="button" className="secondary" disabled={busy} onClick={closeEditor}>
                {editing ? 'Cancel editing' : 'Cancel'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </section>
  );
}
