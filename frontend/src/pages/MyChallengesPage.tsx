import { useEffect, useState, useMemo } from 'react';
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

type VisibilityFilter = 'all' | 'public' | 'private';

function filterChallenges(
  list: ChallengeSummaryResponse[],
  search: string,
  visibility: VisibilityFilter,
  platform: string
): ChallengeSummaryResponse[] {
  const q = search.trim().toLowerCase();
  return list.filter((c) => {
    if (q && !c.title.toLowerCase().includes(q)) return false;
    if (visibility === 'public' && !c.isPublic) return false;
    if (visibility === 'private' && c.isPublic) return false;
    if (platform && (c.platformOrigin ?? '') !== platform) return false;
    return true;
  });
}

export default function MyChallengesPage() {
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [list, setList] = useState<ChallengeSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [visibilityFilter, setVisibilityFilter] = useState<VisibilityFilter>('all');
  const [platformFilter, setPlatformFilter] = useState('');

  const platforms = useMemo(() => {
    const set = new Set<string>();
    list.forEach((c) => {
      if (c.platformOrigin?.trim()) set.add(c.platformOrigin.trim());
    });
    return Array.from(set).sort();
  }, [list]);

  const filteredList = useMemo(
    () => filterChallenges(list, search, visibilityFilter, platformFilter),
    [list, search, visibilityFilter, platformFilter]
  );

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

  if (loading) {
    return (
      <div className="py-12 text-center text-gray-500">
        A carregar...
      </div>
    );
  }
  if (error) {
    return (
      <div className="py-8">
        <div className="p-4 rounded-lg bg-red-50 text-red-700">{error}</div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold tracking-tight text-slate-900">Meus desafios</h1>
        <Link
          to="/challenges/new"
          className="inline-flex items-center justify-center px-4 py-2 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg shadow-sm transition-colors"
        >
          + Novo desafio
        </Link>
      </div>

      {list.length > 0 && (
        <div className="mb-6 p-4 bg-white rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
          <label className="block text-sm font-medium text-slate-700">Filtros e busca</label>
          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="search"
              placeholder="Buscar por título..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="flex-1 min-w-0 px-3 py-2 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900"
            />
            <select
              value={visibilityFilter}
              onChange={(e) => setVisibilityFilter(e.target.value as VisibilityFilter)}
              className="px-3 py-2 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary bg-white text-slate-900"
            >
              <option value="all">Todos (público/privado)</option>
              <option value="public">Apenas públicos</option>
              <option value="private">Apenas privados</option>
            </select>
            <select
              value={platformFilter}
              onChange={(e) => setPlatformFilter(e.target.value)}
              className="px-3 py-2 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary bg-white text-slate-900 min-w-[140px]"
            >
              <option value="">Todas as plataformas</option>
              {platforms.map((p) => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </div>
          {filteredList.length !== list.length && (
            <p className="text-sm text-gray-500">
              Mostrando {filteredList.length} de {list.length} desafios.
            </p>
          )}
        </div>
      )}

      {list.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-8 text-center text-slate-600">
          <p>Ainda não tem desafios.</p>
          <Link
            to="/challenges/new"
            className="mt-2 inline-block text-codearchive-primary hover:underline font-medium"
          >
            Criar o primeiro
          </Link>
        </div>
      ) : filteredList.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-8 text-center text-slate-600">
          Nenhum desafio corresponde aos filtros. Tente alterar a busca ou os filtros.
        </div>
      ) : (
        <ul className="space-y-3 list-none p-0 m-0">
          {filteredList.map((c) => (
            <li
              key={c.id}
              className="bg-white border border-slate-200/80 rounded-2xl shadow-sm hover:shadow-md transition-shadow"
            >
              <Link
                to={`/challenges/${c.id}`}
                className="block p-4 no-underline text-gray-900 hover:text-codearchive-primary transition-colors"
              >
                <strong className="font-semibold">{c.title}</strong>
                {c.platformOrigin && (
                  <span className="ml-2 text-gray-500">({c.platformOrigin})</span>
                )}
                <span className="ml-2 text-sm text-gray-500">
                  {c.isPublic ? 'Público' : 'Privado'} · {formatDate(c.createdAt)}
                </span>
                {(c.timeComplexity != null || c.spaceComplexity != null || c.aiAutonomyIndex != null) && (
                  <div className="mt-1.5 text-sm text-gray-500">
                    {c.timeComplexity && <span>Tempo: {c.timeComplexity}</span>}
                    {c.spaceComplexity && (
                      <span>{c.timeComplexity ? ' · ' : ''}Espaço: {c.spaceComplexity}</span>
                    )}
                    {c.aiAutonomyIndex != null && (
                      <span>
                        {c.timeComplexity || c.spaceComplexity ? ' · ' : ''}
                        IA: {c.aiAutonomyIndex}/5
                      </span>
                    )}
                  </div>
                )}
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
