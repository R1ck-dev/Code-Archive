import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Layout() {
  const { token, logout } = useAuth();

  return (
    <div>
      <nav>
        <Link to="/">CodeArchive</Link>
        <Link to="/portfolio">Portfólio</Link>
        {token ? (
          <>
            <Link to="/challenges">Meus desafios</Link>
            <button type="button" onClick={logout}>
              Sair
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Registo</Link>
          </>
        )}
      </nav>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
