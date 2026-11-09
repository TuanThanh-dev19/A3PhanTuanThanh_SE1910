// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, errorMessage } from '../api.js';
import BookingEditor from '../components/BookingEditor.jsx';
import Notice from '../components/Notice.jsx';
import Modal from '../components/Modal.jsx';

export default function CustomerBooking() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [editorOpen, setEditorOpen] = useState(false);
  const navigate = useNavigate();
  useEffect(() => {
    const controller = new AbortController();
    api
      .get('/rooms', { signal: controller.signal })
      .then(({ data }) => setRooms(data.filter((room) => room.roomStatus === 1)))
      .catch((failure) => {
        if (!controller.signal.aborted) setError(errorMessage(failure));
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, []);

  async function save(body) {
    setError('');
    setBusy(true);
    try {
      const { data } = await api.post('/bookings', body);
      navigate('/history', {
        state: {
          message: `Booking #${data.bookingReservationId} created successfully.`,
          bookingId: data.bookingReservationId,
        },
      });
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }

  function closeEditor() {
    setEditorOpen(false);
    setError('');
  }

  return (
    <section>
      <h1>Book your stay</h1>
      <p>
        Select one or more rooms and choose the dates for each stay. Availability is checked when
        you save.
      </p>
      <Notice error={!editorOpen ? error : ''} />
      {loading && <p role="status">Loading active rooms...</p>}
      {!loading && rooms.length > 0 && (
        <button
          onClick={() => {
            setError('');
            setEditorOpen(true);
          }}
        >
          Create booking
        </button>
      )}
      {editorOpen && (
        <Modal title="Create booking" wide busy={busy} onClose={closeEditor}>
          <Notice error={error} />
          <BookingEditor rooms={rooms} onSave={save} onCancel={closeEditor} busy={busy} />
        </Modal>
      )}
      {!loading && !error && rooms.length === 0 && <p>No active rooms are available to book.</p>}
    </section>
  );
}
