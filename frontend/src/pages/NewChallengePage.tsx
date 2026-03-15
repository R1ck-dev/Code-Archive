import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { submitChallenge, ApiError } from '../api';
import type { SnippetRequest } from '../api';

export default function NewChallengePage() {
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [title, setTitle] = useState('');
  const [platformOrigin, setPlatformOrigin] = useState('');
  const [sourceCode, setSourceCode] = useState('');
  const [timeComplexity, setTimeComplexity] = useState('');
  const [spaceComplexity, setSpaceComplexity] = useState('');
  const [aiAutonomyIndex, setAiAutonomyIndex] = useState<number>(3);
  const [snippets, setSnippets] = useState<SnippetRequest[]>([{ code: '', description: '', conceptCategory: '' }]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  function addSnippet() {
    setSnippets((s) => [...s, { code: '', description: '', conceptCategory: '' }]);
  }

  function updateSnippet(i: number, field: keyof SnippetRequest, value: string) {
    setSnippets((s) => {
      const next = [...s];
      next[i] = { ...next[i], [field]: value };
      return next;
    });
  }

  function removeSnippet(i: number) {
    setSnippets((s) => s.filter((_, j) => j !== i));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    const snippetList = snippets
      .filter((s) => s.code.trim() !== '')
      .map((s) => ({
        code: s.code.trim(),
        description: s.description?.trim() || undefined,
        conceptCategory: s.conceptCategory?.trim() || undefined,
      }));
    try {
      const res = await submitChallenge(
        {
          title: title.trim(),
          platformOrigin: platformOrigin.trim() || undefined,
          sourceCode: sourceCode.trim(),
          timeComplexity: timeComplexity.trim() || undefined,
          spaceComplexity: spaceComplexity.trim() || undefined,
          aiAutonomyIndex: aiAutonomyIndex >= 1 && aiAutonomyIndex <= 5 ? aiAutonomyIndex : undefined,
          snippets: snippetList.length > 0 ? snippetList : undefined,
        },
        getToken
      );
      navigate(`/challenges/${res.id}`, { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          logout();
          navigate('/login', { replace: true });
          return;
        }
        setError(err.message);
      } else {
        setError('Erro ao criar desafio.');
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ maxWidth: 700, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Novo desafio</h1>
      <p><Link to="/challenges">← Voltar</Link></p>
      <form onSubmit={handleSubmit}>
        {error && (
          <div style={{ color: 'crimson', marginBottom: '0.5rem' }}>{error}</div>
        )}
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="title">Título *</label>
          <input
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="platform">Plataforma (ex: LeetCode, Codewars)</label>
          <input
            id="platform"
            value={platformOrigin}
            onChange={(e) => setPlatformOrigin(e.target.value)}
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="sourceCode">Código-fonte *</label>
          <textarea
            id="sourceCode"
            value={sourceCode}
            onChange={(e) => setSourceCode(e.target.value)}
            required
            rows={12}
            style={{ display: 'block', width: '100%', padding: '0.5rem', fontFamily: 'monospace' }}
          />
        </div>
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
          <div style={{ flex: 1 }}>
            <label htmlFor="time">Complexidade temporal</label>
            <input
              id="time"
              value={timeComplexity}
              onChange={(e) => setTimeComplexity(e.target.value)}
              placeholder="ex: O(n)"
              style={{ display: 'block', width: '100%', padding: '0.5rem' }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <label htmlFor="space">Complexidade espacial</label>
            <input
              id="space"
              value={spaceComplexity}
              onChange={(e) => setSpaceComplexity(e.target.value)}
              placeholder="ex: O(1)"
              style={{ display: 'block', width: '100%', padding: '0.5rem' }}
            />
          </div>
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <label htmlFor="aiIndex">Índice de autonomia IA (1–5)</label>
          <input
            id="aiIndex"
            type="number"
            min={1}
            max={5}
            value={aiAutonomyIndex}
            onChange={(e) => setAiAutonomyIndex(Number(e.target.value) || 3)}
            style={{ display: 'block', width: '100%', padding: '0.5rem' }}
          />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <strong>Snippets (aprendizados)</strong>
            <button type="button" onClick={addSnippet}>+ Adicionar snippet</button>
          </div>
          {snippets.map((s, i) => (
            <div
              key={i}
              style={{
                border: '1px solid #ddd',
                borderRadius: 8,
                padding: '1rem',
                marginTop: '0.5rem',
              }}
            >
              <div style={{ marginBottom: '0.5rem' }}>
                <label>Código do snippet</label>
                <textarea
                  value={s.code}
                  onChange={(e) => updateSnippet(i, 'code', e.target.value)}
                  rows={3}
                  style={{ display: 'block', width: '100%', padding: '0.5rem', fontFamily: 'monospace' }}
                />
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <label>Descrição</label>
                <input
                  value={s.description ?? ''}
                  onChange={(e) => updateSnippet(i, 'description', e.target.value)}
                  style={{ display: 'block', width: '100%', padding: '0.5rem' }}
                />
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <label>Categoria do conceito</label>
                <input
                  value={s.conceptCategory ?? ''}
                  onChange={(e) => updateSnippet(i, 'conceptCategory', e.target.value)}
                  style={{ display: 'block', width: '100%', padding: '0.5rem' }}
                />
              </div>
              <button
                type="button"
                onClick={() => removeSnippet(i)}
                style={{ marginTop: '0.25rem' }}
              >
                Remover snippet
              </button>
            </div>
          ))}
        </div>

        <button type="submit" disabled={loading} style={{ padding: '0.5rem 1rem' }}>
          {loading ? 'A guardar...' : 'Criar desafio'}
        </button>
      </form>
    </div>
  );
}
