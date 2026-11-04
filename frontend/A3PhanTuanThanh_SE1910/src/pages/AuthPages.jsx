// Author: Phan Tuan Thanh - CE190595
import {useState} from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import {api, errorMessage} from '../api.js';
import {useAuth} from '../AuthContext.jsx';
import ProfileFields, {emptyProfile} from '../components/ProfileFields.jsx';
import Notice from '../components/Notice.jsx';

export function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);
    const {login} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    async function submit(event) {
        event.preventDefault();
        setError('');
        setBusy(true);
        try {
            const {data} = await api.post('/auth/login', {email, password});
            login(data);
            navigate(data.role === 'STAFF' ? '/staff/customers' : '/', {replace: true});
        } catch (failure) {
            setError(errorMessage(failure));
        } finally {
            setBusy(false);
        }
    }

    return (
        <section className="auth-panel">
            <h1>Sign in</h1>
            <Notice error={error} message={location.state?.message}/>
            <form onSubmit={submit}>
                <label>
                    Email
                    <input
                        required
                        type="email"
                        maxLength={50}
                        autoComplete="username"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />
                </label>
                <label>
                    Password
                    <input
                        required
                        type="password"
                        maxLength={64}
                        autoComplete="current-password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                </label>
                <button disabled={busy}>{busy ? 'Signing in...' : 'Sign in'}</button>
            </form>
            <p>
                New here? <Link to="/register">Create an account</Link>
            </p>
        </section>
    );
}

export function RegisterPage() {
    const [profile, setProfile] = useState(emptyProfile);
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);
    const navigate = useNavigate();

    async function submit(event) {
        event.preventDefault();
        setError('');
        setBusy(true);
        try {
            await api.post('/auth/register', {profile, password});
            navigate('/login', {state: {message: 'Account created. Please sign in.'}});
        } catch (failure) {
            setError(errorMessage(failure));
        } finally {
            setBusy(false);
        }
    }

    return (
        <section>
            <h1>Create an account</h1>
            <Notice error={error}/>
            <form className="panel" onSubmit={submit}>
                <ProfileFields value={profile} onChange={setProfile}/>
                <label>
                    Password
                    <input
                        required
                        type="password"
                        minLength={8}
                        maxLength={64}
                        autoComplete="new-password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                </label>
                <p>Use 8 to 64 characters, with no more than 72 UTF-8 bytes.</p>
                <button disabled={busy}>{busy ? 'Creating account...' : 'Create account'}</button>
            </form>
        </section>
    );
}
