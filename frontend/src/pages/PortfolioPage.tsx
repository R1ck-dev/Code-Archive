import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { listPublicChallengesByAuthor, ApiError } from '../api';
import type { ChallengeSummaryResponse } from '../api';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
}

export default function PortfolioPage() {
  const { authorId } = useParams<{ authorId: string }>();
  const [list, setList] = useState<ChallengeSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authorId) return;
    let cancelled = false;
    (async () => {
      try {
        const data = await listPublicChallengesByAuthor(authorId);
        if (!cancelled) setList(data);
      } catch (err) {
        if (!cancelled && err instanceof ApiError) setError(err.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [authorId]);

  if (!authorId) {
    return (
      <div style={{ padding: '2rem' }}>
        <p>ID do autor em falta.</p>
        <Link to="/portfolio">Ver portfólio</Link>
      </div>
    );
  }

  if (loading) return <div style={{ padding: '2rem' }}>A carregar...</div>;
  if (error) {
    return (
      <div style={{ padding: '2rem' }}>
        <p style={{ color: 'crimson' }}>{error}</p>
        <Link to="/portfolio">Voltar</Link>
      </div>
    );
  }

  return (
    <div style={{ padding: '2rem', maxWidth: 900, margin: '0 auto' }}>
      <h1>Portfólio público</h1>
      <p><Link to="/portfolio">← Introduzir outro ID de autor</Link></p>
      {list.length === 0 ? (
        <p>Este utilizador ainda não tem desafios públicos.</p>
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
                {formatDate(c.createdAt)}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
