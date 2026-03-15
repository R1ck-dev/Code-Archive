// Types aligned with backend DTOs (API responses and requests)

export interface TokenResponse {
  acessToken: string; // backend typo
}

export interface UserProfileResponse {
  id: string;
  username: string;
  isPublic: boolean;
  createdAt: string;
}

export interface ChallengeResponse {
  id: string;
  title: string;
  isPublic: boolean;
  createdAt: string;
}

export interface ChallengeSummaryResponse {
  id: string;
  title: string;
  platformOrigin: string | null;
  timeComplexity: string | null;
  spaceComplexity: string | null;
  aiAutonomyIndex: number | null;
  isPublic: boolean;
  createdAt: string;
}

export interface SnippetDetailResponse {
  id: string;
  code: string;
  description: string | null;
  conceptCategory: string | null;
  createdAt: string;
}

export interface ChallengeDetailResponse {
  id: string;
  authorId: string;
  title: string;
  platformOrigin: string | null;
  sourceCode: string;
  timeComplexity: string | null;
  spaceComplexity: string | null;
  aiAutonomyIndex: number | null;
  isPublic: boolean;
  createdAt: string;
  updatedAt: string;
  snippets: SnippetDetailResponse[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterUserRequest {
  username: string;
  email: string;
  password: string;
}

export interface SnippetRequest {
  code: string;
  description?: string;
  conceptCategory?: string;
}

export interface SubmitChallengeRequest {
  title: string;
  platformOrigin?: string;
  sourceCode: string;
  timeComplexity?: string;
  spaceComplexity?: string;
  aiAutonomyIndex?: number;
  snippets?: SnippetRequest[];
}

export interface ChangeVisibilityRequest {
  isPublic: boolean;
}
