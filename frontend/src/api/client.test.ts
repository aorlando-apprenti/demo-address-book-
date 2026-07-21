import { describe, it, expect, vi, afterEach } from 'vitest';
import { authApi, adminApi, ApiRequestError } from './client';

function mockFetchOnce(status: number, body: unknown) {
  const ok = status >= 200 && status < 300;
  globalThis.fetch = vi.fn().mockResolvedValue({
    ok,
    status,
    headers: { get: () => 'application/json' },
    json: async () => body,
  }) as unknown as typeof fetch;
}

function lastCallInit(): RequestInit {
  const mockFn = fetch as unknown as ReturnType<typeof vi.fn>;
  return mockFn.mock.calls[0][1] as RequestInit;
}

describe('authApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('register posts payload and returns parsed user', async () => {
    mockFetchOnce(201, { id: 1, email: 'a@b.com' });

    const result = await authApi.register({
      email: 'a@b.com',
      password: 'pw',
      address: 'addr',
      telephoneNumber: '123',
    });

    expect(result).toEqual({ id: 1, email: 'a@b.com' });
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/auth/register',
      expect.objectContaining({ method: 'POST' }),
    );
  });

  it('login throws ApiRequestError on failure with message', async () => {
    mockFetchOnce(401, { message: 'Invalid email or password' });

    await expect(authApi.login({ email: 'a@b.com', password: 'wrong' })).rejects.toThrow(ApiRequestError);
  });

  it('changePassword sends bearer token header', async () => {
    mockFetchOnce(200, { message: 'ok' });

    await authApi.changePassword({ oldPassword: 'old', newPassword: 'new' }, 'tok123');

    const headers = lastCallInit().headers as Record<string, string>;
    expect(headers.Authorization).toBe('Bearer tok123');
  });
});

describe('adminApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('createUser posts payload with auth header', async () => {
    mockFetchOnce(201, { user: { id: 2, email: 'x@y.com' }, temporaryPassword: 'temp123' });

    const result = await adminApi.createUser({ email: 'x@y.com', address: 'a', telephoneNumber: '1' }, 'tok');

    expect(result.temporaryPassword).toBe('temp123');
  });

  it('removeUser sends a DELETE request', async () => {
    mockFetchOnce(200, { message: 'removed' });

    const result = await adminApi.removeUser(5, 'tok');

    expect(result.message).toBe('removed');
    expect(lastCallInit().method).toBe('DELETE');
  });

  it('resetPassword returns the new password payload', async () => {
    mockFetchOnce(200, { userId: 5, email: 'x@y.com', newPassword: 'newpw' });

    const result = await adminApi.resetPassword(5, 'tok');

    expect(result.newPassword).toBe('newpw');
  });

  it('propagates field errors from validation failures', async () => {
    mockFetchOnce(400, { message: 'Validation failed', fieldErrors: { email: 'Email is required' } });

    await expect(
      adminApi.createUser({ email: '', address: '', telephoneNumber: '' }, 'tok'),
    ).rejects.toMatchObject({ fieldErrors: { email: 'Email is required' } });
  });
});
