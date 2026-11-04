// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { api, errorMessage } from '../api.js';
import { useAuth } from '../AuthContext.jsx';
import ProfileFields from '../components/ProfileFields.jsx';
import Notice from '../components/Notice.jsx';
export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);
  const { updateName } = useAuth();
  function fields(data) {
    return {
      customerFullName: data.customerFullName || '',
      telephone: data.telephone || '',
      emailAddress: data.emailAddress,
      customerBirthday: data.customerBirthday || '',
    };
  }
  useEffect(() => {
    const controller = new AbortController();
    api
      .get('/profile', { signal: controller.signal })
      .then(({ data }) => setProfile(fields(data)))
      .catch((failure) => {
        if (!controller.signal.aborted) setError(errorMessage(failure));
      });
    return () => controller.abort();
  }, []);
  async function save(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    setBusy(true);
    try {
      const { data } = await api.put('/profile', profile);
      setProfile(fields(data));
      updateName(data.customerFullName);
      setMessage('Profile updated.');
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }
  return (
    <section>
      <h1>My profile</h1>
      <Notice error={error} message={message} />
      {!profile && !error && <p role="status">Loading profile...</p>}
      {profile && (
        <form className="panel" onSubmit={save}>
          <ProfileFields value={profile} onChange={setProfile} />
          <button disabled={busy}>{busy ? 'Saving...' : 'Save profile'}</button>
        </form>
      )}
    </section>
  );
}
