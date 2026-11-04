// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { api, errorMessage, money } from '../api.js';
import Notice from '../components/Notice.jsx';
const blank = { roomNumber: '', roomDetailDescription: '', roomMaxCapacity: 2, roomTypeId: '', roomStatus: 1, roomPricePerDay: '' };
export default function StaffRooms() {
  const [rows, setRows] = useState([]); const [types, setTypes] = useState([]); const [search, setSearch] = useState('');
  const [form, setForm] = useState(blank); const [editing, setEditing] = useState(null); const [removing, setRemoving] = useState(null);
  const [error, setError] = useState(''); const [message, setMessage] = useState(''); const [busy, setBusy] = useState(false);
  async function load(term = '') { try { setRows((await api.get('/staff/rooms', { params: { search: term } })).data); } catch (failure) { setError(errorMessage(failure)); } }
  useEffect(() => { load(); api.get('/room-types').then(({ data }) => setTypes(data)).catch((failure) => setError(errorMessage(failure))); }, []);
  const field = (name) => ({ value: form[name] ?? '', onChange: (event) => setForm({ ...form, [name]: event.target.value }) });
  function edit(room) { setEditing(room?.roomId || null); setForm(room || blank); setError(''); setMessage(''); }
  async function save(event) {
    event.preventDefault(); setError(''); setMessage(''); setBusy(true);
    const body = { ...form, roomMaxCapacity: Number(form.roomMaxCapacity), roomTypeId: Number(form.roomTypeId), roomStatus: Number(form.roomStatus), roomPricePerDay: Number(form.roomPricePerDay) };
    try { if (editing) await api.put(`/staff/rooms/${editing}`, body); else await api.post('/staff/rooms', body); const wasEdit = Boolean(editing); edit(null); await load(search); setMessage(wasEdit ? 'Room updated.' : 'Room created.'); }
    catch (failure) { setError(errorMessage(failure)); } finally { setBusy(false); }
  }
  async function remove() { setError(''); setBusy(true); try { const { data } = await api.delete(`/staff/rooms/${removing.roomId}`); setMessage(data.message); setRemoving(null); await load(search); } catch (failure) { setError(errorMessage(failure)); } finally { setBusy(false); } }
  return <section><h1>Manage rooms</h1><Notice error={error} message={message} />
    <form className="search" onSubmit={(event) => { event.preventDefault(); load(search); }}><label>Search rooms<input maxLength={100} value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Room number or description" /></label><button>Search</button></form>
    <div className="table-scroll"><table><thead><tr><th>Room</th><th>Type</th><th>Capacity</th><th>Price / night</th><th>Status</th><th>Actions</th></tr></thead><tbody>{rows.map((room) => <tr key={room.roomId}><td>{room.roomNumber}</td><td>{room.roomTypeName}</td><td>{room.roomMaxCapacity}</td><td>{money(room.roomPricePerDay)}</td><td>{room.roomStatus === 1 ? 'Active' : 'Inactive'}</td><td className="actions"><button className="secondary" onClick={() => edit(room)}>Edit</button><button className="danger" onClick={() => setRemoving(room)}>Delete / deactivate</button></td></tr>)}</tbody></table></div>{rows.length === 0 && <p>No matching rooms.</p>}
    {removing && <div className="panel"><p>Delete room {removing.roomNumber}? Rooms with booking history will be deactivated.</p><button className="danger" disabled={busy} onClick={remove}>Confirm room deletion</button> <button className="secondary" onClick={() => setRemoving(null)}>Keep room</button></div>}
    <form className="panel" onSubmit={save}><h2>{editing ? 'Edit room' : 'Add room'}</h2><div className="form-grid">
      <label>Room number<input required maxLength={50} {...field('roomNumber')} /></label><label>Room description<input maxLength={220} {...field('roomDetailDescription')} /></label>
      <label>Capacity<input required type="number" min={1} step={1} {...field('roomMaxCapacity')} /></label>
      <label>Room type<select aria-label="Room type" required {...field('roomTypeId')}><option value="">Select type</option>{types.map((type) => <option key={type.roomTypeId} value={type.roomTypeId}>{type.roomTypeName}</option>)}</select></label>
      <label>Room status<select aria-label="Room status" {...field('roomStatus')}><option value={1}>Active</option><option value={2}>Inactive</option></select></label>
      <label>Price per night<input required type="number" min={0.01} max={1000000000} step={0.0001} {...field('roomPricePerDay')} /></label>
    </div><div className="actions"><button disabled={busy}>{busy ? 'Saving...' : editing ? 'Save room' : 'Add room'}</button>{editing && <button type="button" className="secondary" onClick={() => edit(null)}>Cancel editing</button>}</div></form>
  </section>;
}
