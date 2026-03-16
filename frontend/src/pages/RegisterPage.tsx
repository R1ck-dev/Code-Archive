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
    <div className="max-w-md mx-auto px-4 py-10">
      <div className="bg-white rounded-2xl shadow-lg border border-slate-200/80 p-6 sm:p-8">
        <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-6">Registro</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="p-3 rounded-lg bg-red-50 text-red-700 text-sm">
              {error}
            </div>
          )}
          <div>
            <label htmlFor="reg-username" className="block text-sm font-medium text-gray-700 mb-1">
              Nome de utilizador
            </label>
            <input
              id="reg-username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={50}
              autoComplete="username"
              className="block w-full px-3 py-2.5 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900"
            />
          </div>
          <div>
            <label htmlFor="reg-email" className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              id="reg-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
              className="block w-full px-3 py-2.5 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900"
            />
          </div>
          <div>
            <label htmlFor="reg-password" className="block text-sm font-medium text-gray-700 mb-1">
              Palavra-passe
            </label>
            <input
              id="reg-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              autoComplete="new-password"
              className="block w-full px-3 py-2.5 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900"
            />
            <p className="mt-1 text-xs text-gray-500">Mínimo 8 caracteres.</p>
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 px-4 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg shadow-sm transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {loading ? 'A carregar...' : 'Criar conta'}
          </button>
        </form>
        <p className="mt-4 text-sm text-gray-600">
          Já tem conta?{' '}
          <Link to="/login" className="text-codearchive-primary hover:underline font-medium">
            Entrar
          </Link>
        </p>
      </div>
    </div>
  );
}
