import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { register, login, ApiError } from '../api';

export default function RegisterPage() {
  const { login: setToken } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await register({ username, email, password });
      const res = await login({ email, password });
      setToken(res.acessToken);
      navigate('/challenges', { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message || `Erro ${err.status}`);
      } else {
        setError('Erro ao criar conta.');
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ maxWidth: 400, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Registo</h1>
      <form onSubmit={handleSubmit}>
        {error && (
          <div style={{ color: 'crimson', marginBottom: '0.5rem' }}>{error}</div>
        )}
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="reg-username">Nome de utilizador</label>
          <input
            id="reg-username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            minLength={3}
            maxLength={50}
            autoComplete="username"
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="reg-email">Email</label>
          <input
            id="reg-email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="reg-password">Palavra-passe</label>
          <input
            id="reg-password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
            autoComplete="new-password"
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
          <small>Mínimo 8 caracteres.</small>
        </div>
        <button type="submit" disabled={loading} style={{ padding: '0.5rem 1rem' }}>
          {loading ? 'A carregar...' : 'Criar conta'}
        </button>
      </form>
      <p style={{ marginTop: '1rem' }}>
        Já tem conta? <Link to="/login">Entrar</Link>
      </p>
    </div>
  );
}
