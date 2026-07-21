import { describe, it, expect, vi, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor } from '@testing-library/react';
import { LoginForm } from './LoginForm';
import { authApi, ApiRequestError } from '../api/client';

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client');
  return {
    ...actual,
    authApi: { register: vi.fn(), login: vi.fn(), changePassword: vi.fn() },
  };
});

describe('LoginForm', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('calls onLogin with the auth response on success', async () => {
    const authResponse = { token: 'tok', tokenType: 'Bearer', email: 'a@b.com', role: 'USER' };
    (authApi.login as ReturnType<typeof vi.fn>).mockResolvedValue(authResponse);
    const onLogin = vi.fn();

    render(<LoginForm onLogin={onLogin} />);
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'a@b.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'pw' } });
    fireEvent.click(screen.getByRole('button', { name: 'Log In' }));

    await waitFor(() => expect(onLogin).toHaveBeenCalledWith(authResponse));
  });

  it('shows an error message on invalid credentials', async () => {
    (authApi.login as ReturnType<typeof vi.fn>).mockRejectedValue(
      new ApiRequestError(401, 'Invalid email or password'),
    );

    render(<LoginForm onLogin={vi.fn()} />);
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'a@b.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrong' } });
    fireEvent.click(screen.getByRole('button', { name: 'Log In' }));

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Invalid email or password'));
  });
});
