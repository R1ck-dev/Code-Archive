import { useEffect, useState, useMemo } from 'react';
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

function filterPortfolio(
  list: ChallengeSummaryResponse[],
  search: string,
  platform: string
): ChallengeSummaryResponse[] {
  const q = search.trim().toLowerCase();
  return list.filter((c) => {
    if (q && !c.title.toLowerCase().includes(q)) return false;
    if (platform && (c.platformOrigin ?? '') !== platform) return false;
    return true;
  });
}

export default function PortfolioPage() {
  const { authorId } = useParams<{ authorId: string }>();
  const [list, setList] = useState<ChallengeSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [platformFilter, setPlatformFilter] = useState('');

  const platforms = useMemo(() => {
    const set = new Set<string>();
    list.forEach((c) => {
      if (c.platformOrigin?.trim()) set.add(c.platformOrigin.trim());
    });
    return Array.from(set).sort();
  }, [list]);

  const filteredList = useMemo(
    () => filterPortfolio(list, search, platformFilter),
    [list, search, platformFilter]
  );

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
      <div className="py-8">
        <p className="text-gray-600">ID do autor em falta.</p>
        <Link to="/portfolio" className="text-codearchive-primary hover:underline mt-2 inline-block">
          Ver portfólio
        </Link>
      </div>
    );
  }

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
        <Link to="/portfolio" className="mt-4 inline-block text-codearchive-primary hover:underline">
          Voltar
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-4">Portfólio público</h1>
      <p className="mb-6">
        <Link to="/portfolio" className="text-gray-600 hover:text-codearchive-primary transition-colors">
          ← Introduzir outro ID de autor
        </Link>
      </p>

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
          Este utilizador ainda não tem desafios públicos.
        </div>
      ) : filteredList.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-8 text-center text-slate-600">
          Nenhum desafio corresponde aos filtros.
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
                  {formatDate(c.createdAt)}
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
