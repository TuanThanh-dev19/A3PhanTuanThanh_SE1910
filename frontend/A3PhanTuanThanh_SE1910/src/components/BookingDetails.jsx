// Author: Phan Tuan Thanh - CE190595
import { money } from '../api.js';
export default function BookingDetails({ booking }) {
  if (!booking) return null;
  return (
    <section className="panel" aria-label="Booking details">
      <h2>Booking #{booking.bookingReservationId}</h2>
      <p>
        {booking.customerName} / {booking.customerEmail}
      </p>
      <p>
        {booking.bookingStatus === 1 ? 'Active' : 'Cancelled'} / Total:{' '}
        <strong>{money(booking.totalPrice)}</strong>
      </p>
      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Room</th>
              <th>Check-in</th>
              <th>Check-out</th>
              <th>Nightly price</th>
              <th>Subtotal</th>
            </tr>
          </thead>
          <tbody>
            {booking.details.map((detail) => (
              <tr key={detail.roomId}>
                <td>{detail.roomNumber}</td>
                <td>{detail.startDate}</td>
                <td>{detail.endDate}</td>
                <td>{money(detail.actualPrice)}</td>
                <td>{money(detail.lineTotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
