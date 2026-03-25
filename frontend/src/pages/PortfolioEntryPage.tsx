import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function PortfolioEntryPage() {
  const navigate = useNavigate();
  const [authorId, setAuthorId] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const trimmed = authorId.trim();
    if (trimmed) navigate(`/portfolio/${trimmed}`);
  }

  return (
    <div className="max-w-md mx-auto px-4 py-10">
      <div className="bg-white rounded-2xl shadow-lg border border-slate-200/80 p-6 sm:p-8">
        <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-2">Ver portfólio público</h1>
        <p className="text-gray-600 text-sm mb-6">
          Introduza o ID do autor para ver os desafios públicos.
        </p>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="authorId" className="block text-sm font-medium text-gray-700 mb-1">
              ID do autor
            </label>
            <input
              id="authorId"
              type="text"
              value={authorId}
              onChange={(e) => setAuthorId(e.target.value)}
              placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
              required
              className="block w-full px-3 py-2.5 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900"
            />
          </div>
          <button
            type="submit"
            className="w-full py-2.5 px-4 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg shadow-sm transition-colors"
          >
            Ver portfólio
          </button>
        </form>
        <p className="mt-4 text-sm text-gray-600">
          <Link to="/" className="text-codearchive-primary hover:underline">
            Voltar
          </Link>
        </p>
      </div>
    </div>
  );
}
