// Author: Phan Tuan Thanh - CE190595
import { useEffect, useRef, useState } from 'react';
import { api, errorMessage } from '../api.js';
import ProfileFields, { emptyProfile } from '../components/ProfileFields.jsx';
import Notice from '../components/Notice.jsx';
export default function StaffCustomers() {
  const [rows, setRows] = useState([]);
  const [search, setSearch] = useState('');
  const [editing, setEditing] = useState(null);
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
      edit(null);
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
      <h1>Manage customers</h1>
      <Notice error={error} message={message} />
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
                  <button className="secondary" onClick={() => edit(customer)}>
                    Edit
                  </button>
                  <button className="danger" onClick={() => setRemoving(customer)}>
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
        <div className="panel">
          <p>
            Delete {removing.customerFullName}? Customers with booking history must be deactivated
            instead.
          </p>
          <button className="danger" disabled={busy} onClick={remove}>
            Confirm customer deletion
          </button>{' '}
          <button className="secondary" onClick={() => setRemoving(null)}>
            Keep customer
          </button>
        </div>
      )}
      <form className="panel" onSubmit={save}>
        <h2>{editing ? 'Edit customer' : 'Add customer'}</h2>
        <ProfileFields value={profile} onChange={setProfile} />
        <div className="form-grid">
          <label>
            Customer status
            <select
              aria-label="Customer status"
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
          {editing && (
            <button type="button" className="secondary" onClick={() => edit(null)}>
              Cancel editing
            </button>
          )}
        </div>
      </form>
    </section>
  );
}
