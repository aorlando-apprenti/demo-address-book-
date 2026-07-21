const BASE_URL = 'http://localhost:8080';

export interface RegisterPayload {
  email: string;
  password: string;
  address: string;
  telephoneNumber: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  email: string;
  role: string;
}

export interface UserResponse {
  id: number;
  email: string;
  address: string;
  telephoneNumber: string;
  role: string;
  createdAt: string;
}

export interface ChangePasswordPayload {
  oldPassword: string;
  newPassword: string;
}

export interface CreateUserPayload {
  email: string;
  address: string;
  telephoneNumber: string;
}

export interface CreateUserResponse {
  user: UserResponse;
  temporaryPassword: string;
}

export interface ResetPasswordResponse {
  userId: number;
  email: string;
  newPassword: string;
}

export interface MessageResponse {
  message: string;
}

export interface ContactPayload {
  name: string;
  address: string;
  telephoneNumber: string;
  email: string;
}

export interface ContactResponse {
  id: number;
  name: string;
  address: string;
  telephoneNumber: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}

export class ApiRequestError extends Error {
  status: number;
  fieldErrors?: Record<string, string>;

  constructor(status: number, message: string, fieldErrors?: Record<string, string>) {
    super(message);
    this.name = 'ApiRequestError';
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> | undefined),
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  const contentType = response.headers.get('content-type');
  const isJson = contentType !== null && contentType.includes('application/json');
  const body = isJson ? await response.json() : undefined;

  if (!response.ok) {
    const message = body && typeof body.message === 'string' ? body.message : 'Request failed';
    const fieldErrors = body && typeof body.fieldErrors === 'object' ? body.fieldErrors : undefined;
    throw new ApiRequestError(response.status, message, fieldErrors);
  }

  return body as T;
}

export const authApi = {
  register: (payload: RegisterPayload): Promise<UserResponse> =>
    request<UserResponse>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),

  login: (payload: LoginPayload): Promise<AuthResponse> =>
    request<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),

  changePassword: (payload: ChangePasswordPayload, token: string): Promise<MessageResponse> =>
    request<MessageResponse>('/account/password', { method: 'PUT', body: JSON.stringify(payload) }, token),
};

export const adminApi = {
  createUser: (payload: CreateUserPayload, token: string): Promise<CreateUserResponse> =>
    request<CreateUserResponse>('/admin/users', { method: 'POST', body: JSON.stringify(payload) }, token),

  removeUser: (id: number, token: string): Promise<MessageResponse> =>
    request<MessageResponse>(`/admin/users/${id}`, { method: 'DELETE' }, token),

  resetPassword: (id: number, token: string): Promise<ResetPasswordResponse> =>
    request<ResetPasswordResponse>(`/admin/users/${id}/reset-password`, { method: 'POST' }, token),
};

export const contactsApi = {
  list: (token: string): Promise<ContactResponse[]> =>
    request<ContactResponse[]>('/contacts', { method: 'GET' }, token),

  search: (query: string, token: string): Promise<ContactResponse[]> =>
    request<ContactResponse[]>(`/contacts/search?query=${encodeURIComponent(query)}`, { method: 'GET' }, token),

  create: (payload: ContactPayload, token: string): Promise<ContactResponse> =>
    request<ContactResponse>('/contacts', { method: 'POST', body: JSON.stringify(payload) }, token),

  update: (id: number, payload: ContactPayload, token: string): Promise<ContactResponse> =>
    request<ContactResponse>(`/contacts/${id}`, { method: 'PUT', body: JSON.stringify(payload) }, token),

  remove: (id: number, token: string): Promise<MessageResponse> =>
    request<MessageResponse>(`/contacts/${id}`, { method: 'DELETE' }, token),
};
