// Author: Phan Tuan Thanh - CE190595
import { useEffect, useRef, useState } from 'react';
import { api, errorMessage, money } from '../api.js';
import BookingEditor from '../components/BookingEditor.jsx';
import BookingDetails from '../components/BookingDetails.jsx';
import Notice from '../components/Notice.jsx';
import Modal from '../components/Modal.jsx';

export default function StaffBookings() {
  const [rows, setRows] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [search, setSearch] = useState('');
  const [editing, setEditing] = useState(undefined);
  const [viewing, setViewing] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);
  const latestSearch = useRef(0);

  async function load(term = '') {
    const requestId = ++latestSearch.current;
    try {
      const { data } = await api.get('/staff/bookings', { params: { search: term } });
      if (requestId === latestSearch.current) setRows(data);
    } catch (failure) {
      if (requestId === latestSearch.current) setError(errorMessage(failure));
    }
  }

  useEffect(() => {
    load();
    Promise.all([api.get('/staff/rooms'), api.get('/staff/customers')])
      .then(([roomData, customerData]) => {
        setRooms(roomData.data);
        setCustomers(customerData.data);
      })
      .catch((failure) => setError(errorMessage(failure)));
  }, []);

  function openEditor(booking) {
    setViewing(null);
    setEditing(booking);
    setError('');
    setMessage('');
  }

  function closeEditor() {
    setEditing(undefined);
    setError('');
  }

  function openDetails(booking) {
    setEditing(undefined);
    setViewing(booking);
    setError('');
    setMessage('');
  }

  async function save(body) {
    setError('');
    setMessage('');
    setBusy(true);
    try {
      const { data } = editing
        ? await api.put(`/staff/bookings/${editing.bookingReservationId}`, body)
        : await api.post('/staff/bookings', body);
      setViewing(data);
      setEditing(undefined);
      setMessage('Booking saved.');
      await load(search);
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }

  async function status(booking) {
    setError('');
    setMessage('');
    setBusy(true);
    try {
      const { data } = await api.patch(`/staff/bookings/${booking.bookingReservationId}/status`, {
        bookingStatus: booking.bookingStatus === 1 ? 2 : 1,
      });
      setViewing(data);
      setMessage(
        data.bookingStatus === 1
          ? 'Booking reactivated.'
          : 'Booking cancelled. History is preserved.',
      );
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
        <h1>Manage bookings</h1>
        <button disabled={busy} onClick={() => openEditor(null)}>
          Add booking
        </button>
      </div>
      <Notice error={editing === undefined && !viewing ? error : ''} message={message} />
      <form
        className="search"
        onSubmit={(event) => {
          event.preventDefault();
          load(search);
        }}
      >
        <label>
          Search bookings
          <input
            maxLength={100}
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Booking ID, customer name or email"
          />
        </label>
        <button>Search</button>
      </form>
      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Booking</th>
              <th>Customer</th>
              <th>Booked on</th>
              <th>Total</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((booking) => (
              <tr key={booking.bookingReservationId}>
                <td>#{booking.bookingReservationId}</td>
                <td>
                  {booking.customerName}
                  <br />
                  {booking.customerEmail}
                </td>
                <td>{booking.bookingDate}</td>
                <td>{money(booking.totalPrice)}</td>
                <td>{booking.bookingStatus === 1 ? 'Active' : 'Cancelled'}</td>
                <td className="actions">
                  <button
                    className="secondary"
                    disabled={busy}
                    onClick={() => openDetails(booking)}
                  >
                    View details
                  </button>
                  <button className="secondary" disabled={busy} onClick={() => openEditor(booking)}>
                    Edit
                  </button>
                  <button className="secondary" disabled={busy} onClick={() => status(booking)}>
                    {booking.bookingStatus === 1 ? 'Cancel booking' : 'Reactivate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {rows.length === 0 && <p>No matching bookings.</p>}
      {viewing && (
        <Modal
          title={`Booking #${viewing.bookingReservationId}`}
          showClose
          wide
          busy={busy}
          onClose={() => {
            setViewing(null);
            setError('');
          }}
        >
          <Notice error={error} message={message} />
          <BookingDetails booking={viewing} />
        </Modal>
      )}
      {editing !== undefined && (
        <Modal
          title={editing ? 'Edit booking' : 'Create booking'}
          wide
          busy={busy}
          onClose={closeEditor}
        >
          <Notice error={error} />
          <BookingEditor
            key={editing?.bookingReservationId || 'new'}
            rooms={rooms}
            customers={customers}
            initial={editing}
            onSave={save}
            onCancel={closeEditor}
            busy={busy}
          />
        </Modal>
      )}
    </section>
  );
}
