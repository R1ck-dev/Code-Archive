import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Layout() {
  const { token, logout } = useAuth();

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <nav className="sticky top-0 z-10 bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex flex-wrap items-center gap-x-8 gap-y-2">
              <Link
                to="/"
                className="text-xl font-bold tracking-tight text-slate-900 hover:text-codearchive-primary transition-colors"
              >
                CodeArchive
              </Link>
              <Link
                to="/portfolio"
                className="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
              >
                Portfólio
              </Link>
              {token && (
                <>
                  <Link
                    to="/challenges"
                    className="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
                  >
                    Meus desafios
                  </Link>
                  <Link
                    to="/profile"
                    className="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
                  >
                    Meu perfil
                  </Link>
                </>
              )}
            </div>
            <div className="flex items-center gap-3">
              {token ? (
                <button
                  type="button"
                  onClick={logout}
                  className="px-4 py-2 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg transition-colors shadow-sm"
                >
                  Sair
                </button>
              ) : (
                <>
                  <Link
                    to="/login"
                    className="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
                  >
                    Login
                  </Link>
                  <Link
                    to="/register"
                    className="px-4 py-2 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg transition-colors shadow-sm"
                  >
                    Registro
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>
      <main className="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <Outlet />
      </main>
    </div>
  );
}
