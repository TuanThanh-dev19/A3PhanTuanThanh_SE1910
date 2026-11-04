// Author: Phan Tuan Thanh - CE190595
const key = 'fuminihotel.session';
export function getSession() {
  try {
    const session = JSON.parse(sessionStorage.getItem(key));
    if (session?.token && ['STAFF', 'CUSTOMER'].includes(session.role) && session.expiresAt > Date.now()) return session;
  } catch { /* Discard invalid local session data. */ }
  sessionStorage.removeItem(key);
  return null;
}
export function saveSession(data) {
  const session = { token: data.token, role: data.role, name: data.name, expiresAt: Date.now() + data.expiresIn * 1000 };
  sessionStorage.setItem(key, JSON.stringify(session));
  return session;
}
export function clearSession() { sessionStorage.removeItem(key); }
