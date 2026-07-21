import { describe, it, expect, vi, afterEach, beforeEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import App from './App';
import { authApi } from './api/client';

vi.mock('./api/client', async () => {
  const actual = await vi.importActual<typeof import('./api/client')>('./api/client');
  return {
    ...actual,
    authApi: { register: vi.fn(), login: vi.fn(), changePassword: vi.fn() },
    adminApi: { createUser: vi.fn(), removeUser: vi.fn(), resetPassword: vi.fn() },
  };
});

async function logIn(email: string, role: string) {
  (authApi.login as ReturnType<typeof vi.fn>).mockResolvedValue({
    token: 'tok',
    tokenType: 'Bearer',
    email,
    role,
  });

  fireEvent.change(screen.getByLabelText('Email'), { target: { value: email } });
  fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password1' } });
  const loginForm = screen.getByRole('form', { hidden: true }) || screen.getByLabelText('login-form');
  fireEvent.click(within(loginForm).getByRole('button', { name: 'Log In' }));

  await waitFor(() => expect(screen.getByText(new RegExp(`Logged in as ${email}`))).toBeInTheDocument());
}

describe('App', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('shows the login form by default', () => {
    render(<App />);

    expect(screen.getByRole('heading', { name: 'Login' })).toBeInTheDocument();
  });

  it('toggles to the registration form', () => {
    render(<App />);

    fireEvent.click(screen.getByRole('button', { name: 'Register' }));

    expect(screen.getByRole('heading', { name: 'Register' })).toBeInTheDocument();
  });

  it('shows change-password but no admin panel after logging in as USER', async () => {
    render(<App />);

    await logIn('user@example.com', 'USER');

    expect(screen.getByRole('heading', { name: 'Change Password' })).toBeInTheDocument();
    expect(screen.queryByRole('heading', { name: 'Admin: User Management' })).not.toBeInTheDocument();
  });

  it('shows the admin panel after logging in as ADMIN', async () => {
    render(<App />);

    await logIn('admin@example.com', 'ADMIN');

    expect(screen.getByRole('heading', { name: 'Admin: User Management' })).toBeInTheDocument();
  });

  it('logs out and returns to the login form', async () => {
    render(<App />);

    await logIn('user@example.com', 'USER');
    fireEvent.click(screen.getByRole('button', { name: 'Log Out' }));

    expect(screen.getByRole('heading', { name: 'Login' })).toBeInTheDocument();
  });
});
