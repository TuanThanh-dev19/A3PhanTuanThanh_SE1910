// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api, errorMessage, money } from '../api.js';
import BookingDetails from '../components/BookingDetails.jsx';
import Notice from '../components/Notice.jsx';
export default function HistoryPage() {
  const [rows, setRows] = useState([]); const [viewing, setViewing] = useState(null);
  const [error, setError] = useState(''); const [loading, setLoading] = useState(true); const location = useLocation();
  async function load() {
    setError(''); setLoading(true);
    try { const { data } = await api.get('/bookings'); setRows(data); setViewing(data.find((booking) => booking.bookingReservationId === location.state?.bookingId) || null); }
    catch (failure) { setError(errorMessage(failure)); } finally { setLoading(false); }
  }
  useEffect(() => { load(); }, []);
  return <section><h1>My booking history</h1><Notice error={error} message={location.state?.message} /><div className="actions"><Link to="/book">Book another stay</Link><button className="secondary" disabled={loading} onClick={load}>Refresh history</button></div>
    {loading && <p role="status">Loading booking history...</p>}
    {!loading && <><div className="table-scroll"><table><thead><tr><th>Booking</th><th>Booked on</th><th>Rooms</th><th>Total</th><th>Status</th><th>Details</th></tr></thead><tbody>{rows.map((booking) => <tr key={booking.bookingReservationId}><td>#{booking.bookingReservationId}</td><td>{booking.bookingDate}</td><td>{booking.details.map((detail) => detail.roomNumber).join(', ')}</td><td>{money(booking.totalPrice)}</td><td>{booking.bookingStatus === 1 ? 'Active' : 'Cancelled'}</td><td><button className="secondary" onClick={() => setViewing(booking)}>View details</button></td></tr>)}</tbody></table></div>{rows.length === 0 && !error && <p>You have no booking reservations yet.</p>}</>}
    <BookingDetails booking={viewing} />
  </section>;
}
