import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { getChallengeDetail, changeChallengeVisibility, ApiError } from '../api';
import type { ChallengeDetailResponse } from '../api';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
}

export default function ChallengeDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [challenge, setChallenge] = useState<ChallengeDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [visibilityLoading, setVisibilityLoading] = useState(false);

  useEffect(() => {
    if (!id) return;
    let cancelled = false;
    (async () => {
      try {
        const data = await getChallengeDetail(id, getToken);
        if (!cancelled) setChallenge(data);
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
  }, [id, getToken, logout, navigate]);

  async function handleToggleVisibility() {
    if (!id || !challenge) return;
    setVisibilityLoading(true);
    try {
      await changeChallengeVisibility(id, { isPublic: !challenge.isPublic }, getToken);
      setChallenge((c) => (c ? { ...c, isPublic: !c.isPublic } : null));
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          logout();
          navigate('/login', { replace: true });
        } else {
          setError(err.message);
        }
      }
    } finally {
      setVisibilityLoading(false);
    }
  }

  if (loading) return <div style={{ padding: '2rem' }}>A carregar...</div>;
  if (error || !challenge) {
    return (
      <div style={{ padding: '2rem' }}>
        <p style={{ color: 'crimson' }}>{error ?? 'Desafio não encontrado.'}</p>
        <Link to="/challenges">Voltar aos meus desafios</Link>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 800, margin: '2rem auto', padding: '0 1rem' }}>
      <p><Link to="/challenges">← Meus desafios</Link></p>
      <h1>{challenge.title}</h1>
      {challenge.platformOrigin && (
        <p style={{ color: '#666' }}>Plataforma: {challenge.platformOrigin}</p>
      )}
      <p>
        {challenge.timeComplexity && `Tempo: ${challenge.timeComplexity}`}
        {challenge.spaceComplexity && ` · Espaço: ${challenge.spaceComplexity}`}
        {challenge.aiAutonomyIndex != null && ` · IA: ${challenge.aiAutonomyIndex}/5`}
      </p>
      <p>
        {challenge.isPublic ? 'Público' : 'Privado'} · Criado em {formatDate(challenge.createdAt)}
      </p>

      <div style={{ marginBottom: '1rem' }}>
        <button
          type="button"
          onClick={handleToggleVisibility}
          disabled={visibilityLoading}
        >
          {visibilityLoading
            ? 'A atualizar...'
            : challenge.isPublic
              ? 'Tornar privado'
              : 'Tornar público'}
        </button>
      </div>

      <section style={{ marginBottom: '2rem' }}>
        <h2>Código-fonte</h2>
        <pre
          style={{
            background: '#f5f5f5',
            padding: '1rem',
            borderRadius: 8,
            overflow: 'auto',
            fontSize: '0.9rem',
          }}
        >
          <code>{challenge.sourceCode}</code>
        </pre>
      </section>

      {challenge.snippets.length > 0 && (
        <section>
          <h2>Snippets / Aprendizados</h2>
          {challenge.snippets.map((s) => (
            <div
              key={s.id}
              style={{
                border: '1px solid #ddd',
                borderRadius: 8,
                padding: '1rem',
                marginBottom: '0.5rem',
              }}
            >
              {s.conceptCategory && (
                <strong style={{ display: 'block', marginBottom: '0.25rem' }}>
                  {s.conceptCategory}
                </strong>
              )}
              {s.description && (
                <p style={{ marginBottom: '0.5rem', color: '#555' }}>{s.description}</p>
              )}
              <pre
                style={{
                  background: '#f5f5f5',
                  padding: '0.5rem',
                  borderRadius: 4,
                  overflow: 'auto',
                  fontSize: '0.85rem',
                }}
              >
                <code>{s.code}</code>
              </pre>
              <small style={{ color: '#888' }}>{formatDate(s.createdAt)}</small>
            </div>
          ))}
        </section>
      )}
    </div>
  );
}
