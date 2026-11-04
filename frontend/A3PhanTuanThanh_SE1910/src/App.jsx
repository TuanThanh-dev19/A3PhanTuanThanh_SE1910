// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { api } from './api.js';

export default function App() {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();
    Promise.all([
      api.get('/rooms', { signal: controller.signal }),
      api.get('/room-types', { signal: controller.signal }),
    ]).then(([roomResponse, typeResponse]) => {
      setRooms(roomResponse.data);
      setRoomTypes(typeResponse.data);
    }).catch((failure) => {
      if (!controller.signal.aborted) {
        setError(failure.response?.data?.message || 'Unable to load rooms. Please try again later.');
      }
    }).finally(() => {
      if (!controller.signal.aborted) setLoading(false);
    });
    return () => controller.abort();
  }, []);

  return (
    <main>
      <header>
        <p className="eyebrow">FUMiniHotelSystem</p>
        <h1>Find your next stay</h1>
        <p>Explore our rooms and choose a space that suits your visit.</p>
      </header>
      {loading && <p role="status">Loading rooms...</p>}
      {error && <p role="alert" className="error">{error}</p>}
      {!loading && !error && (
        <section aria-label="Room catalog">
          <div className="catalog-heading">
            <h2>Our rooms</h2>
            <p>{rooms.length} rooms across {roomTypes.length} room types</p>
          </div>
          {rooms.length === 0 && <p>No rooms are available to display.</p>}
          <div className="rooms">
            {rooms.map((room) => (
              <article key={room.roomId}>
                <p className="eyebrow">{room.roomTypeName}</p>
                <h3>Room {room.roomNumber}</h3>
                <p>{room.roomDetailDescription}</p>
                <p>Up to {room.roomMaxCapacity} guests</p>
                <p className="price">{new Intl.NumberFormat('en-US', {
                  style: 'currency', currency: 'VND', maximumFractionDigits: 0,
                }).format(room.roomPricePerDay)} <span>/ night</span></p>
                <span className="status">{room.roomStatus === 1 ? 'Active' : 'Inactive'}</span>
              </article>
            ))}
          </div>
        </section>
      )}
      <footer>Room catalog preview. Account and booking features are planned for the next stages.</footer>
    </main>
  );
}
