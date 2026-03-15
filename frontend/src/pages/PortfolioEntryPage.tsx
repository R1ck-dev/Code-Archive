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
    <div style={{ maxWidth: 400, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Ver portfólio público</h1>
      <p>Introduza o ID (UUID) do autor para ver os desafios públicos.</p>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="authorId">ID do autor</label>
          <input
            id="authorId"
            type="text"
            value={authorId}
            onChange={(e) => setAuthorId(e.target.value)}
            placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
            required
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>
        <button type="submit" style={{ padding: '0.5rem 1rem' }}>
          Ver portfólio
        </button>
      </form>
      <p style={{ marginTop: '1rem' }}>
        <Link to="/">Voltar</Link>
      </p>
    </div>
  );
}
