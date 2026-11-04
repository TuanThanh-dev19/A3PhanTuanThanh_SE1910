// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { api, errorMessage, money } from '../api.js';
import Notice from '../components/Notice.jsx';
export default function RoomsPage() {
  const [rooms, setRooms] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  useEffect(() => {
    const controller = new AbortController();
    api.get('/rooms', { signal: controller.signal }).then(({ data }) => setRooms(data))
      .catch((failure) => { if (!controller.signal.aborted) setError(errorMessage(failure)); })
      .finally(() => { if (!controller.signal.aborted) setLoading(false); });
    return () => controller.abort();
  }, []);
  return <section><header className="hero"><p className="eyebrow">Welcome to FUMiniHotelSystem</p><h1>Find your next stay</h1><p>Explore our rooms and choose a space that suits your visit.</p></header>
    <Notice error={error} />{loading && <p role="status">Loading rooms...</p>}
    {!loading && !error && <><div className="catalog-heading"><h2>Our rooms</h2><p>{rooms.length} rooms</p></div><div className="rooms">{rooms.map((room) => <article key={room.roomId}><p className="eyebrow">{room.roomTypeName}</p><h3>Room {room.roomNumber}</h3><p>{room.roomDetailDescription}</p><p>Up to {room.roomMaxCapacity} guests</p><p className="price">{money(room.roomPricePerDay)} <span>/ night</span></p><span className="status">{room.roomStatus === 1 ? 'Active' : 'Inactive'}</span></article>)}</div>{rooms.length === 0 && <p>No rooms are available to display.</p>}</>}
  </section>;
}
