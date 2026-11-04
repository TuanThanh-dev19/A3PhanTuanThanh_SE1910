// Author: Phan Tuan Thanh - CE190595
export default function Notice({ error, message }) {
  return <>{error && <p role="alert" className="error">{error}</p>}{message && <p role="status" className="success">{message}</p>}</>;
}
