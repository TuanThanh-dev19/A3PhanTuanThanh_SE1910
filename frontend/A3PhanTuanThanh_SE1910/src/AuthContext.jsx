// Author: Phan Tuan Thanh - CE190595
import { createContext, useContext, useEffect, useState } from 'react';
import { getSession, saveSession, clearSession } from './session.js';
const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [session, setSession] = useState(getSession);
  useEffect(() => {
    const expire = () => setSession(null);
    window.addEventListener('session-expired', expire);
    return () => window.removeEventListener('session-expired', expire);
  }, []);
  useEffect(() => {
    if (!session) return;
    const timer = setTimeout(() => { clearSession(); setSession(null); }, Math.max(0, session.expiresAt - Date.now()));
    return () => clearTimeout(timer);
  }, [session]);
  function login(data) { setSession(saveSession(data)); }
  function logout() { clearSession(); setSession(null); }
  return <AuthContext.Provider value={{ session, login, logout }}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
