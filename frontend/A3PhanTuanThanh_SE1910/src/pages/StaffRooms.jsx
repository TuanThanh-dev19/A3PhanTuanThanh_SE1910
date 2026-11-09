// Author: Phan Tuan Thanh - CE190595
import { useEffect, useRef, useState } from 'react';
import { api, errorMessage, money } from '../api.js';
import Notice from '../components/Notice.jsx';
import Modal from '../components/Modal.jsx';

const blank = {
  roomNumber: '',
  roomDetailDescription: '',
  roomMaxCapacity: 2,
  roomTypeId: '',
  roomStatus: 1,
  roomPricePerDay: '',
};
export default function StaffRooms() {
  const [rows, setRows] = useState([]);
  const [types, setTypes] = useState([]);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState(blank);
  const [editing, setEditing] = useState(null);
  const [editorOpen, setEditorOpen] = useState(false);
  const [removing, setRemoving] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);
  const latestSearch = useRef(0);

  async function load(term = '') {
    const requestId = ++latestSearch.current;
    try {
      const { data } = await api.get('/staff/rooms', { params: { search: term } });
      if (requestId === latestSearch.current) setRows(data);
    } catch (failure) {
      if (requestId === latestSearch.current) setError(errorMessage(failure));
    }
  }

  useEffect(() => {
    load();
    api
      .get('/room-types')
      .then(({ data }) => setTypes(data))
      .catch((failure) => setError(errorMessage(failure)));
  }, []);
  const field = (name) => ({
    disabled: busy,
    value: form[name] ?? '',
    onChange: (event) => setForm({ ...form, [name]: event.target.value }),
  });

  function edit(room) {
    setEditing(room?.roomId || null);
    setForm(room ? { ...room } : { ...blank });
    setRemoving(null);
    setEditorOpen(true);
    setError('');
    setMessage('');
  }

  function closeEditor() {
    setEditorOpen(false);
    setEditing(null);
    setForm({ ...blank });
    setError('');
  }

  function confirmRemoval(room) {
    setEditorOpen(false);
    setRemoving(room);
    setError('');
    setMessage('');
  }

  async function save(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    setBusy(true);
    const body = {
      ...form,
      roomMaxCapacity: Number(form.roomMaxCapacity),
      roomTypeId: Number(form.roomTypeId),
      roomStatus: Number(form.roomStatus),
      roomPricePerDay: Number(form.roomPricePerDay),
    };
    try {
      if (editing) await api.put(`/staff/rooms/${editing}`, body);
      else await api.post('/staff/rooms', body);
      const wasEdit = Boolean(editing);
      closeEditor();
      await load(search);
      setMessage(wasEdit ? 'Room updated.' : 'Room created.');
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
      const { data } = await api.delete(`/staff/rooms/${removing.roomId}`);
      setMessage(data.message);
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
        <h1>Manage rooms</h1>
        <button onClick={() => edit(null)} disabled={busy}>
          Add room
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
          Search rooms
          <input
            maxLength={100}
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Room number or description"
          />
        </label>
        <button>Search</button>
      </form>
      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Room</th>
              <th>Type</th>
              <th>Capacity</th>
              <th>Price / night</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((room) => (
              <tr key={room.roomId}>
                <td>{room.roomNumber}</td>
                <td>{room.roomTypeName}</td>
                <td>{room.roomMaxCapacity}</td>
                <td>{money(room.roomPricePerDay)}</td>
                <td>{room.roomStatus === 1 ? 'Active' : 'Inactive'}</td>
                <td className="actions">
                  <button className="secondary" disabled={busy} onClick={() => edit(room)}>
                    Edit
                  </button>
                  <button className="danger" disabled={busy} onClick={() => confirmRemoval(room)}>
                    Delete / deactivate
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {rows.length === 0 && <p>No matching rooms.</p>}
      {removing && (
        <Modal
          title="Delete or deactivate room"
          compact
          busy={busy}
          onClose={() => setRemoving(null)}
        >
          <Notice error={error} />
          <p>Delete room {removing.roomNumber}? Rooms with booking history will be deactivated.</p>
          <div className="actions">
            <button className="danger" disabled={busy} onClick={remove}>
              Confirm room deletion
            </button>
            <button
              className="secondary"
              data-modal-focus
              disabled={busy}
              onClick={() => setRemoving(null)}
            >
              Keep room
            </button>
          </div>
        </Modal>
      )}
      {editorOpen && (
        <Modal title={editing ? 'Edit room' : 'Add room'} busy={busy} onClose={closeEditor}>
          <Notice error={error} />
          <form onSubmit={save}>
            <div className="form-grid">
              <label>
                Room number
                <input data-modal-focus required maxLength={50} {...field('roomNumber')} />
              </label>
              <label>
                Room description
                <input maxLength={220} {...field('roomDetailDescription')} />
              </label>
              <label>
                Capacity
                <input required type="number" min={1} step={1} {...field('roomMaxCapacity')} />
              </label>
              <label>
                Room type
                <select aria-label="Room type" required {...field('roomTypeId')}>
                  <option value="">Select type</option>
                  {types.map((type) => (
                    <option key={type.roomTypeId} value={type.roomTypeId}>
                      {type.roomTypeName}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Room status
                <select aria-label="Room status" {...field('roomStatus')}>
                  <option value={1}>Active</option>
                  <option value={2}>Inactive</option>
                </select>
              </label>
              <label>
                Price per night
                <input
                  required
                  type="number"
                  min={0.01}
                  max={1000000000}
                  step={0.0001}
                  {...field('roomPricePerDay')}
                />
              </label>
            </div>
            <div className="actions">
              <button disabled={busy}>
                {busy ? 'Saving...' : editing ? 'Save room' : 'Add room'}
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
