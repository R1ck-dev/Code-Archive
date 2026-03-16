import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { submitChallenge, ApiError } from '../api';
import type { SnippetRequest } from '../api';

const inputClass =
  'block w-full px-3 py-2 border border-slate-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary text-slate-900';
const labelClass = 'block text-sm font-medium text-slate-700 mb-1';

const CONCEPT_CATEGORIES = [
  'Estrutura de Dados',
  'Matemática',
  'Algoritmos',
  'Strings',
  'Grafos',
  'Ordenação e Busca',
  'Programação Dinâmica',
  'Recursão',
  'POO',
  'Concorrência',
  'Bases de Dados',
  'Redes',
  'Segurança',
  'Outro',
] as const;

export default function NewChallengePage() {
  const navigate = useNavigate();
  const { getToken, logout } = useAuth();
  const [title, setTitle] = useState('');
  const [platformOrigin, setPlatformOrigin] = useState('');
  const [sourceCode, setSourceCode] = useState('');
  const [timeComplexity, setTimeComplexity] = useState('');
  const [spaceComplexity, setSpaceComplexity] = useState('');
  const [aiAutonomyIndex, setAiAutonomyIndex] = useState<number>(3);
  const [snippets, setSnippets] = useState<SnippetRequest[]>([
    { code: '', description: '', conceptCategory: '' },
  ]);
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
    <div className="max-w-3xl mx-auto">
      <p className="mb-4">
        <Link
          to="/challenges"
          className="text-gray-600 hover:text-codearchive-primary transition-colors"
        >
          ← Voltar
        </Link>
      </p>
      <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-6">Novo desafio</h1>
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && (
          <div className="p-3 rounded-lg bg-red-50 text-red-700 text-sm">
            {error}
          </div>
        )}
        <div>
          <label htmlFor="title" className={labelClass}>
            Título *
          </label>
          <input
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className={inputClass}
          />
        </div>
        <div>
          <label htmlFor="platform" className={labelClass}>
            Plataforma (ex: LeetCode, Codewars)
          </label>
          <input
            id="platform"
            value={platformOrigin}
            onChange={(e) => setPlatformOrigin(e.target.value)}
            className={inputClass}
          />
        </div>
        <div>
          <label htmlFor="sourceCode" className={labelClass}>
            Código-fonte *
          </label>
          <textarea
            id="sourceCode"
            value={sourceCode}
            onChange={(e) => setSourceCode(e.target.value)}
            required
            rows={12}
            className={`${inputClass} font-mono text-sm bg-gray-50`}
          />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="time" className={labelClass}>
              Complexidade temporal
            </label>
            <input
              id="time"
              value={timeComplexity}
              onChange={(e) => setTimeComplexity(e.target.value)}
              placeholder="ex: O(n)"
              className={inputClass}
            />
          </div>
          <div>
            <label htmlFor="space" className={labelClass}>
              Complexidade espacial
            </label>
            <input
              id="space"
              value={spaceComplexity}
              onChange={(e) => setSpaceComplexity(e.target.value)}
              placeholder="ex: O(1)"
              className={inputClass}
            />
          </div>
        </div>
        <div>
          <label htmlFor="aiIndex" className={labelClass}>
            Índice de autonomia IA (1–5)
          </label>
          <input
            id="aiIndex"
            type="number"
            min={1}
            max={5}
            value={aiAutonomyIndex}
            onChange={(e) => setAiAutonomyIndex(Number(e.target.value) || 3)}
            className="w-20 px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-codearchive-primary focus:border-codearchive-primary"
          />
        </div>

        <div>
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 mb-3">
            <strong className="text-gray-900">Snippets (aprendizados)</strong>
            <button
              type="button"
              onClick={addSnippet}
              className="px-3 py-1.5 text-sm font-medium text-codearchive-primary border border-codearchive-primary rounded-lg hover:bg-blue-50 transition-colors"
            >
              + Adicionar snippet
            </button>
          </div>
          <div className="space-y-3">
            {snippets.map((s, i) => (
              <div
                key={i}
                className="border border-gray-200 rounded-lg p-4 bg-gray-50/50 space-y-3"
              >
                <div>
                  <label className={labelClass}>Código do snippet</label>
                  <textarea
                    value={s.code}
                    onChange={(e) => updateSnippet(i, 'code', e.target.value)}
                    rows={3}
                    className={`${inputClass} font-mono text-sm bg-white`}
                  />
                </div>
                <div>
                  <label className={labelClass}>Descrição</label>
                  <input
                    value={s.description ?? ''}
                    onChange={(e) => updateSnippet(i, 'description', e.target.value)}
                    className={inputClass}
                  />
                </div>
                <div>
                  <label className={labelClass}>Categoria do conceito</label>
                  <select
                    value={s.conceptCategory ?? ''}
                    onChange={(e) => updateSnippet(i, 'conceptCategory', e.target.value)}
                    className={inputClass}
                  >
                    <option value="">Selecione...</option>
                    {CONCEPT_CATEGORIES.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                </div>
                <button
                  type="button"
                  onClick={() => removeSnippet(i)}
                  className="text-sm text-gray-600 hover:text-red-600 transition-colors"
                >
                  Remover snippet
                </button>
              </div>
            ))}
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="py-2.5 px-6 text-sm font-medium text-white bg-codearchive-primary hover:bg-codearchive-primary-hover rounded-lg shadow-sm transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
        >
          {loading ? 'A guardar...' : 'Criar desafio'}
        </button>
      </form>
    </div>
  );
}
