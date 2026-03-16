import type {
  TokenResponse,
  UserProfileResponse,
  ChallengeResponse,
  ChallengeSummaryResponse,
  ChallengeDetailResponse,
  LoginRequest,
  RegisterUserRequest,
  SubmitChallengeRequest,
  ChangeVisibilityRequest,
} from './types';

export const API_BASE = '/api/v1';

export type GetToken = () => string | null;

async function request<T>(
  path: string,
  options: RequestInit & { token?: string | null } = {}
): Promise<T> {
  const { token, ...init } = options;
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(init.headers as Record<string, string>),
  };
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }
  const res = await fetch(`${API_BASE}${path}`, { ...init, headers });
  if (!res.ok) {
    const text = await res.text();
    let message = text;
    try {
      const json = JSON.parse(text);
      if (json.message) message = json.message;
    } catch {
      // use text as message
    }
    throw new ApiError(res.status, message);
  }
  if (res.status === 204) return undefined as unknown as T;
  return res.json() as Promise<T>;
}

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

// Auth
export async function getProfile(getToken: GetToken): Promise<UserProfileResponse> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<UserProfileResponse>('/users/me', { token });
}

export async function updateProfileVisibility(
  body: { isPublic: boolean },
  getToken: GetToken
): Promise<void> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<void>('/users/me', {
    method: 'PATCH',
    body: JSON.stringify(body),
    token,
  });
}

export async function login(body: LoginRequest): Promise<TokenResponse> {
  return request<TokenResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

export async function register(
  body: RegisterUserRequest
): Promise<UserProfileResponse> {
  const res = await fetch(`${API_BASE}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text();
    let message = text;
    try {
      const json = JSON.parse(text);
      if (json.message) message = json.message;
    } catch {
      //
    }
    throw new ApiError(res.status, message);
  }
  return res.json() as Promise<UserProfileResponse>;
}

// Challenges
export async function listMyChallenges(
  getToken: GetToken
): Promise<ChallengeSummaryResponse[]> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<ChallengeSummaryResponse[]>('/challenges', { token });
}

export async function submitChallenge(
  body: SubmitChallengeRequest,
  getToken: GetToken
): Promise<ChallengeResponse> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<ChallengeResponse>('/challenges', {
    method: 'POST',
    body: JSON.stringify(body),
    token,
  });
}

export async function getChallengeDetail(
  id: string,
  getToken: GetToken
): Promise<ChallengeDetailResponse> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<ChallengeDetailResponse>(`/challenges/${id}`, { token });
}

export async function changeChallengeVisibility(
  id: string,
  body: ChangeVisibilityRequest,
  getToken: GetToken
): Promise<void> {
  const token = getToken();
  if (!token) throw new ApiError(401, 'Not authenticated');
  return request<void>(`/challenges/${id}/visibility`, {
    method: 'PATCH',
    body: JSON.stringify(body),
    token,
  });
}

export async function listPublicChallengesByAuthor(
  authorId: string
): Promise<ChallengeSummaryResponse[]> {
  return request<ChallengeSummaryResponse[]>(`/challenges/user/${authorId}`);
}
