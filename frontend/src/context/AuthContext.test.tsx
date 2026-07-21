import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent } from '@testing-library/react';
import { AuthProvider, useAuth } from './AuthContext';

function Consumer() {
  const { auth, login, logout } = useAuth();
  return (
    <div>
      <p data-testid="status">{auth ? `${auth.email}:${auth.role}` : 'anonymous'}</p>
      <button type="button" onClick={() => login({ token: 't1', email: 'a@b.com', role: 'USER' })}>
        login
      </button>
      <button type="button" onClick={logout}>
        logout
      </button>
    </div>
  );
}

function Bare() {
  useAuth();
  return null;
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
  });

  it('starts unauthenticated when no stored auth exists', () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>,
    );

    expect(screen.getByTestId('status').textContent).toBe('anonymous');
  });

  it('login updates state and persists to localStorage', () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>,
    );

    fireEvent.click(screen.getByText('login'));

    expect(screen.getByTestId('status').textContent).toBe('a@b.com:USER');
    expect(localStorage.getItem('addressbook.auth')).toContain('a@b.com');
  });

  it('logout clears state and localStorage', () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>,
    );

    fireEvent.click(screen.getByText('login'));
    fireEvent.click(screen.getByText('logout'));

    expect(screen.getByTestId('status').textContent).toBe('anonymous');
    expect(localStorage.getItem('addressbook.auth')).toBeNull();
  });

  it('restores auth from localStorage on mount', () => {
    localStorage.setItem(
      'addressbook.auth',
      JSON.stringify({ token: 't2', email: 'c@d.com', role: 'ADMIN' }),
    );

    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>,
    );

    expect(screen.getByTestId('status').textContent).toBe('c@d.com:ADMIN');
  });

  it('throws when useAuth is used outside of an AuthProvider', () => {
    expect(() => render(<Bare />)).toThrow('useAuth must be used within an AuthProvider');
  });
});
