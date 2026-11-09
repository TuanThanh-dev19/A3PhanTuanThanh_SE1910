// Author: Phan Tuan Thanh - CE190595
import { useEffect, useState } from 'react';
import { api, errorMessage } from '../api.js';
import { useAuth } from '../AuthContext.jsx';
import ProfileFields from '../components/ProfileFields.jsx';
import Notice from '../components/Notice.jsx';
import Modal from '../components/Modal.jsx';
export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [draft, setDraft] = useState(null);
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
      const { data } = await api.put('/profile', draft);
      setProfile(fields(data));
      updateName(data.customerFullName);
      setDraft(null);
      setMessage('Profile updated.');
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }
  return (
    <section>
      <div className="catalog-heading">
        <h1>My profile</h1>
        {profile && (
          <button
            onClick={() => {
              setDraft({ ...profile });
              setError('');
              setMessage('');
            }}
          >
            Edit profile
          </button>
        )}
      </div>
      <Notice error={!draft ? error : ''} message={message} />
      {!profile && !error && <p role="status">Loading profile...</p>}
      {profile && (
        <dl className="panel form-grid profile-summary">
          <div>
            <dt>Full name</dt>
            <dd>{profile.customerFullName}</dd>
          </div>
          <div>
            <dt>Telephone</dt>
            <dd>{profile.telephone}</dd>
          </div>
          <div>
            <dt>Email address</dt>
            <dd>{profile.emailAddress}</dd>
          </div>
          <div>
            <dt>Birthday</dt>
            <dd>{profile.customerBirthday}</dd>
          </div>
        </dl>
      )}
      {draft && (
        <Modal
          title="Edit profile"
          busy={busy}
          onClose={() => {
            setDraft(null);
            setError('');
          }}
        >
          <Notice error={error} />
          <form onSubmit={save}>
            <ProfileFields value={draft} onChange={setDraft} disabled={busy} />
            <div className="actions">
              <button disabled={busy}>{busy ? 'Saving...' : 'Save profile'}</button>
              <button
                type="button"
                className="secondary"
                disabled={busy}
                onClick={() => {
                  setDraft(null);
                  setError('');
                }}
              >
                Cancel editing
              </button>
            </div>
          </form>
        </Modal>
      )}
    </section>
  );
}
