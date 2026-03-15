import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { listMyChallenges, ApiError } from '../api';
import type { ChallengeSummaryResponse } from '../api';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
}

export default function MyChallengesPage() {
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [list, setList] = useState<ChallengeSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await listMyChallenges(getToken);
        if (!cancelled) setList(data);
      } catch (err) {
        if (!cancelled) {
          if (err instanceof ApiError && err.status === 401) {
            logout();
            navigate('/login', { replace: true });
          } else if (err instanceof ApiError) {
            setError(err.message);
          }
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [getToken, logout, navigate]);

  if (loading) return <div style={{ padding: '2rem' }}>A carregar...</div>;
  if (error) return <div style={{ padding: '2rem', color: 'crimson' }}>{error}</div>;

  return (
    <div style={{ padding: '2rem', maxWidth: 900, margin: '0 auto' }}>
      <h1>Meus desafios</h1>
      <p>
        <Link to="/challenges/new">+ Novo desafio</Link>
      </p>
      {list.length === 0 ? (
        <p>Ainda não tem desafios. <Link to="/challenges/new">Criar o primeiro</Link>.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {list.map((c) => (
            <li
              key={c.id}
              style={{
                border: '1px solid #ccc',
                borderRadius: 8,
                padding: '1rem',
                marginBottom: '0.5rem',
              }}
            >
              <Link to={`/challenges/${c.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                <strong>{c.title}</strong>
              </Link>
              {c.platformOrigin && (
                <span style={{ marginLeft: '0.5rem', color: '#666' }}>({c.platformOrigin})</span>
              )}
              <span style={{ marginLeft: '0.5rem', fontSize: '0.9rem' }}>
                {c.isPublic ? 'Público' : 'Privado'} · {formatDate(c.createdAt)}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
