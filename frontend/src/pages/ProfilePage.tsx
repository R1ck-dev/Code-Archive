import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { getProfile, updateProfileVisibility, ApiError } from '../api';
import type { UserProfileResponse } from '../api';

export default function ProfilePage() {
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [profile, setProfile] = useState<UserProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [visibilityLoading, setVisibilityLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await getProfile(getToken);
        if (!cancelled) setProfile(data);
      } catch (err) {
        if (!cancelled) {
          if (err instanceof ApiError && err.status === 401) {
            logout();
            navigate('/login', { replace: true });
          } else {
            setError(err instanceof Error ? err.message : 'Erro inesperado ao carregar perfil.');
          }
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [getToken, logout, navigate]);

  async function handleToggleVisibility() {
    if (!profile) return;
    setVisibilityLoading(true);
    try {
      await updateProfileVisibility({ isPublic: !profile.isPublic }, getToken);
      setProfile((p) => (p ? { ...p, isPublic: !p.isPublic } : null));
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        logout();
        navigate('/login', { replace: true });
      } else {
        setError(err instanceof ApiError ? err.message : 'Erro ao atualizar.');
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
  if (error && !profile) {
    return (
      <div className="py-8">
        <div className="p-4 rounded-lg bg-red-50 text-red-700">{error}</div>
      </div>
    );
  }
  if (!profile) return null;

  return (
    <div className="max-w-md mx-auto">
      <div className="bg-white rounded-2xl shadow-lg border border-slate-200/80 p-6 sm:p-8">
        <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-6">Meu perfil</h1>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-500 mb-1">Nome de utilizador</label>
            <p className="text-gray-900 font-medium">{profile.username}</p>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-500 mb-1">ID do utilizador</label>
            <p className="text-sm font-mono text-gray-700 break-all bg-gray-50 px-3 py-2 rounded-lg">
              {profile.id}
            </p>
            <p className="mt-1 text-xs text-gray-500">
              Partilhe este ID para outros verem o seu portfólio público em{' '}
              <Link to="/portfolio" className="text-codearchive-primary hover:underline">
                Portfólio
              </Link>
              .
            </p>
          </div>
          <div>
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-medium text-gray-700">Perfil público</p>
                <p className="text-xs text-gray-500">
                  {profile.isPublic
                    ? 'Outros podem ver os seus desafios públicos pelo seu ID.'
                    : 'O seu portfólio não está visível para outros.'}
                </p>
              </div>
              <button
                type="button"
                onClick={handleToggleVisibility}
                disabled={visibilityLoading}
                className="px-4 py-2 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg shadow-sm transition-colors disabled:opacity-60"
              >
                {visibilityLoading
                  ? 'A atualizar...'
                  : profile.isPublic
                    ? 'Tornar privado'
                    : 'Tornar público'}
              </button>
            </div>
          </div>
        </div>

        <div className="mt-6 pt-6 border-t border-gray-200 flex gap-4">
          <Link
            to={`/portfolio/${profile.id}`}
            className="text-sm text-codearchive-primary hover:underline"
          >
            Ver meu portfólio
          </Link>
          <Link to="/challenges" className="text-sm text-gray-600 hover:text-gray-900">
            Meus desafios
          </Link>
        </div>
      </div>
    </div>
  );
}
