// Author: Phan Tuan Thanh - CE190595
import { useState } from 'react';
import { money } from '../api.js';
import { localDate } from '../date.js';

export function blankLine() {
  return {
    roomId: '',
    startDate: localDate(),
    endDate: localDate(1),
  };
}

export default function BookingEditor({ rooms, customers, initial, onSave, onCancel, busy }) {
  const [lines, setLines] = useState(
    initial?.details?.map((line) => ({
      roomId: line.roomId,
      startDate: line.startDate,
      endDate: line.endDate,
    })) || [blankLine()],
  );
  const [customerId, setCustomerId] = useState(initial?.customerId || '');
  const [bookingStatus, setBookingStatus] = useState(initial?.bookingStatus || 1);
  const [error, setError] = useState('');

  function change(index, field, value) {
    setLines((current) =>
      current.map((line, i) => (i === index ? { ...line, [field]: value } : line)),
    );
  }

  async function submit(event) {
    event.preventDefault();
    setError('');
    if (new Set(lines.map((line) => Number(line.roomId))).size !== lines.length) {
      setError('Select each room only once.');
      return;
    }
    if (lines.some((line) => line.endDate <= line.startDate)) {
      setError('Checkout must be after check-in for every room.');
      return;
    }
    await onSave({
      rooms: lines.map((line) => ({ ...line, roomId: Number(line.roomId) })),
      ...(customers
        ? { customerId: Number(customerId), bookingStatus: Number(bookingStatus) }
        : {}),
    });
  }

  const estimate = lines.reduce((total, line) => {
    const nights = (new Date(line.endDate) - new Date(line.startDate)) / 86400000;
    return (
      total +
      Math.max(0, nights || 0) *
        (rooms.find((room) => room.roomId === Number(line.roomId))?.roomPricePerDay || 0)
    );
  }, 0);
  return (
    <form onSubmit={submit}>
      <fieldset className="editor-fields" disabled={busy}>
        {customers && (
          <div className="form-grid">
            <label>
              Customer
              <select
                aria-label="Customer"
                data-modal-focus
                required
                value={customerId}
                onChange={(event) => setCustomerId(event.target.value)}
              >
                <option value="">Select customer</option>
                {customers
                  .filter(
                    (customer) =>
                      customer.customerStatus === 1 || customer.customerId === Number(customerId),
                  )
                  .map((customer) => (
                    <option key={customer.customerId} value={customer.customerId}>
                      {customer.customerFullName} ({customer.emailAddress})
                    </option>
                  ))}
              </select>
            </label>
            <label>
              Booking status
              <select
                aria-label="Booking status"
                value={bookingStatus}
                onChange={(event) => setBookingStatus(event.target.value)}
              >
                <option value={1}>Active</option>
                <option value={2}>Cancelled</option>
              </select>
            </label>
          </div>
        )}
        {lines.map((line, index) => (
          <fieldset key={index}>
            <legend>Room {index + 1}</legend>
            <div className="form-grid">
              <label>
                Room {index + 1}
                <select
                  aria-label={`Room ${index + 1}`}
                  data-modal-focus={!customers && index === 0 ? true : undefined}
                  required
                  value={line.roomId}
                  onChange={(event) => change(index, 'roomId', event.target.value)}
                >
                  <option value="">Select room</option>
                  {rooms.map((room) => (
                    <option
                      key={room.roomId}
                      value={room.roomId}
                      disabled={room.roomStatus !== 1 && room.roomId !== Number(line.roomId)}
                    >
                      Room {room.roomNumber} / {money(room.roomPricePerDay)}
                      {room.roomStatus !== 1 ? '  / Inactive' : ''}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Check-in {index + 1}
                <input
                  required
                  type="date"
                  min={localDate()}
                  value={line.startDate}
                  onChange={(event) => change(index, 'startDate', event.target.value)}
                  onInput={(event) => change(index, 'startDate', event.currentTarget.value)}
                />
              </label>
              <label>
                Check-out {index + 1}
                <input
                  required
                  type="date"
                  min={line.startDate}
                  value={line.endDate}
                  onChange={(event) => change(index, 'endDate', event.target.value)}
                  onInput={(event) => change(index, 'endDate', event.currentTarget.value)}
                />
              </label>
            </div>
            {lines.length > 1 && (
              <button
                type="button"
                className="secondary"
                onClick={() => setLines(lines.filter((_, i) => i !== index))}
              >
                Remove room {index + 1}
              </button>
            )}
          </fieldset>
        ))}
        {lines.length < 20 && (
          <button
            type="button"
            className="secondary"
            onClick={() => setLines([...lines, blankLine()])}
          >
            Add another room
          </button>
        )}
        <p>
          Estimated total: <strong>{money(estimate)}</strong>. The final total is calculated when
          you save.
        </p>
        {error && (
          <p className="error" role="alert">
            {error}
          </p>
        )}
        <div className="actions">
          <button disabled={busy}>{busy ? 'Saving...' : 'Save booking'}</button>
          <button type="button" className="secondary" onClick={onCancel}>
            Cancel editing
          </button>
        </div>
      </fieldset>
    </form>
  );
}
