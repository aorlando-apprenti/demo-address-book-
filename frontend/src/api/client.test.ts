import { describe, it, expect, vi, afterEach } from 'vitest';
import { authApi, adminApi, contactsApi, ApiRequestError } from './client';

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

function lastCallUrl(): string {
  const mockFn = fetch as unknown as ReturnType<typeof vi.fn>;
  return mockFn.mock.calls[0][0] as string;
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
      addressLine1: 'addr',
      city: 'Springfield',
      state: 'IL',
      zipCode: '62701',
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

    const result = await adminApi.createUser(
      { email: 'x@y.com', addressLine1: 'a', city: 'Springfield', state: 'IL', zipCode: '62701', telephoneNumber: '1' },
      'tok',
    );

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
      adminApi.createUser(
        { email: '', addressLine1: '', city: '', state: '', zipCode: '', telephoneNumber: '' },
        'tok',
      ),
    ).rejects.toMatchObject({ fieldErrors: { email: 'Email is required' } });
  });
});

describe('contactsApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('list sends a GET request with the bearer token', async () => {
    mockFetchOnce(200, [{ id: 1, name: 'Alice' }]);

    const result = await contactsApi.list('tok');

    expect(result).toEqual([{ id: 1, name: 'Alice' }]);
    expect(lastCallUrl()).toBe('http://localhost:8080/contacts');
    const headers = lastCallInit().headers as Record<string, string>;
    expect(headers.Authorization).toBe('Bearer tok');
  });

  it('search encodes the query string and calls the search endpoint', async () => {
    mockFetchOnce(200, [{ id: 2, name: 'Bob Jones' }]);

    const result = await contactsApi.search('bob jones', 'tok');

    expect(result).toEqual([{ id: 2, name: 'Bob Jones' }]);
    expect(lastCallUrl()).toBe('http://localhost:8080/contacts/search?query=bob%20jones');
  });

  it('create posts the contact payload', async () => {
    mockFetchOnce(201, { id: 3, name: 'Carol' });

    const result = await contactsApi.create(
      { name: 'Carol', addressLine1: 'addr', telephoneNumber: '1', email: 'c@example.com' },
      'tok',
    );

    expect(result).toEqual({ id: 3, name: 'Carol' });
    expect(lastCallInit().method).toBe('POST');
  });

  it('update sends a PUT request to the contact id', async () => {
    mockFetchOnce(200, { id: 3, name: 'Carol Updated' });

    const result = await contactsApi.update(
      3,
      { name: 'Carol Updated', addressLine1: 'addr', telephoneNumber: '1', email: 'c@example.com' },
      'tok',
    );

    expect(result.name).toBe('Carol Updated');
    expect(lastCallUrl()).toBe('http://localhost:8080/contacts/3');
    expect(lastCallInit().method).toBe('PUT');
  });

  it('remove sends a DELETE request to the contact id', async () => {
    mockFetchOnce(200, { message: 'Contact deleted successfully.' });

    const result = await contactsApi.remove(3, 'tok');

    expect(result.message).toBe('Contact deleted successfully.');
    expect(lastCallUrl()).toBe('http://localhost:8080/contacts/3');
    expect(lastCallInit().method).toBe('DELETE');
  });

  it('propagates errors from a failed request', async () => {
    mockFetchOnce(404, { message: 'Resource not found' });

    await expect(contactsApi.remove(999, 'tok')).rejects.toThrow(ApiRequestError);
  });
});
