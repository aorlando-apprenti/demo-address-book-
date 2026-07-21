import { useState } from 'react';
import './App.css';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginForm } from './components/LoginForm';
import { RegisterForm } from './components/RegisterForm';
import { ChangePasswordForm } from './components/ChangePasswordForm';
import { AdminUserManagement } from './components/AdminUserManagement';
import { ContactManagement } from './components/ContactManagement';

function AuthenticatedView() {
  const { auth, logout } = useAuth();

  if (!auth) {
    return null;
  }

  return (
    <div>
      <header>
        <p>
          Logged in as {auth.email} ({auth.role})
        </p>
        <button type="button" onClick={logout}>
          Log Out
        </button>
      </header>
      <ChangePasswordForm token={auth.token} />
      <ContactManagement token={auth.token} />
      {auth.role === 'ADMIN' && <AdminUserManagement token={auth.token} />}
    </div>
  );
}

function UnauthenticatedView() {
  const { login } = useAuth();
  const [mode, setMode] = useState<'login' | 'register'>('login');

  return (
    <div>
      <nav>
        <button type="button" onClick={() => setMode('login')} disabled={mode === 'login'}>
          Log In
        </button>
        <button type="button" onClick={() => setMode('register')} disabled={mode === 'register'}>
          Register
        </button>
      </nav>
      {mode === 'login' ? (
        <LoginForm onLogin={login} />
      ) : (
        <RegisterForm onRegistered={() => setMode('login')} />
      )}
    </div>
  );
}

function AppShell() {
  const { auth } = useAuth();
  return auth ? <AuthenticatedView /> : <UnauthenticatedView />;
}

function App() {
  return (
    <AuthProvider>
      <section id="app-root">
        <h1>Address Book</h1>
        <AppShell />
      </section>
    </AuthProvider>
  );
}

export default App;
