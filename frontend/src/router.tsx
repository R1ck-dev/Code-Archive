import { createBrowserRouter, Navigate } from 'react-router-dom';
import { ProtectedRoute } from './auth/ProtectedRoute';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MyChallengesPage from './pages/MyChallengesPage';
import NewChallengePage from './pages/NewChallengePage';
import ChallengeDetailPage from './pages/ChallengeDetailPage';
import PortfolioPage from './pages/PortfolioPage';
import PortfolioEntryPage from './pages/PortfolioEntryPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/challenges" replace /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      { path: 'portfolio', element: <PortfolioEntryPage /> },
      {
        path: 'challenges',
        element: (
          <ProtectedRoute>
            <MyChallengesPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'challenges/new',
        element: (
          <ProtectedRoute>
            <NewChallengePage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'challenges/:id',
        element: (
          <ProtectedRoute>
            <ChallengeDetailPage />
          </ProtectedRoute>
        ),
      },
      { path: 'portfolio/:authorId', element: <PortfolioPage /> },
    ],
  },
]);
