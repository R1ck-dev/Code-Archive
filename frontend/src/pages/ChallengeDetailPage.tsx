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

  if (loading) {
    return (
      <div className="py-12 text-center text-gray-500">
        A carregar...
      </div>
    );
  }
  if (error || !challenge) {
    return (
      <div className="py-8">
        <p className="p-4 rounded-lg bg-red-50 text-red-700">
          {error ?? 'Desafio não encontrado.'}
        </p>
        <Link
          to="/challenges"
          className="mt-4 inline-block text-codearchive-primary hover:underline"
        >
          Voltar aos meus desafios
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <p className="mb-4">
        <Link
          to="/challenges"
          className="text-gray-600 hover:text-codearchive-primary transition-colors"
        >
          ← Meus desafios
        </Link>
      </p>
      <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-2">{challenge.title}</h1>
      {challenge.platformOrigin && (
        <p className="text-gray-500 mb-1">Plataforma: {challenge.platformOrigin}</p>
      )}
      <p className="text-gray-600 text-sm mb-4">
        {challenge.timeComplexity && `Tempo: ${challenge.timeComplexity}`}
        {challenge.spaceComplexity && ` · Espaço: ${challenge.spaceComplexity}`}
        {challenge.aiAutonomyIndex != null && ` · IA: ${challenge.aiAutonomyIndex}/5`}
      </p>
      <p className="text-sm text-gray-500 mb-6">
        {challenge.isPublic ? 'Público' : 'Privado'} · Criado em {formatDate(challenge.createdAt)}
      </p>

      <div className="mb-6">
        <button
          type="button"
          onClick={handleToggleVisibility}
          disabled={visibilityLoading}
          className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-60"
        >
          {visibilityLoading
            ? 'A atualizar...'
            : challenge.isPublic
              ? 'Tornar privado'
              : 'Tornar público'}
        </button>
      </div>

      <section className="mb-8">
        <h2 className="text-lg font-semibold text-gray-900 mb-3">Código-fonte</h2>
        <pre className="bg-gray-100 rounded-lg p-4 overflow-x-auto text-sm font-mono text-gray-800 border border-gray-200">
          <code>{challenge.sourceCode}</code>
        </pre>
      </section>

      {challenge.snippets.length > 0 && (
        <section>
          <h2 className="text-lg font-semibold text-gray-900 mb-3">Snippets / Aprendizados</h2>
          <div className="space-y-3">
            {challenge.snippets.map((s) => (
              <div
                key={s.id}
                className="border border-gray-200 rounded-lg p-4 bg-white shadow-sm"
              >
                {s.conceptCategory && (
                  <strong className="block text-gray-900 mb-1">{s.conceptCategory}</strong>
                )}
                {s.description && (
                  <p className="mb-2 text-gray-600 text-sm">{s.description}</p>
                )}
                <pre className="bg-gray-100 rounded p-3 overflow-x-auto text-xs font-mono text-gray-800 border border-gray-200">
                  <code>{s.code}</code>
                </pre>
                <small className="text-gray-400 text-xs">{formatDate(s.createdAt)}</small>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
