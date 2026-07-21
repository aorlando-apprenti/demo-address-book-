import { useState, type FormEvent } from 'react';
import { authApi, ApiRequestError } from '../api/client';

export interface RegisterFormProps {
  onRegistered?: () => void;
}

export function RegisterForm({ onRegistered }: RegisterFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [address, setAddress] = useState('');
  const [telephoneNumber, setTelephoneNumber] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setSuccess(false);
    setSubmitting(true);
    try {
      await authApi.register({ email, password, address, telephoneNumber });
      setSuccess(true);
      onRegistered?.();
    } catch (err) {
      setError(err instanceof ApiRequestError ? err.message : 'Registration failed');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} aria-label="register-form">
      <h2>Register</h2>

      <label htmlFor="register-email">Email</label>
      <input
        id="register-email"
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />

      <label htmlFor="register-password">Password</label>
      <input
        id="register-password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />

      <label htmlFor="register-address">Address</label>
      <input
        id="register-address"
        type="text"
        value={address}
        onChange={(e) => setAddress(e.target.value)}
        required
      />

      <label htmlFor="register-phone">Telephone Number</label>
      <input
        id="register-phone"
        type="text"
        value={telephoneNumber}
        onChange={(e) => setTelephoneNumber(e.target.value)}
        required
      />

      <button type="submit" disabled={submitting}>
        Register
      </button>

      {error && <p role="alert">{error}</p>}
      {success && <p role="status">Registration successful. You can now log in.</p>}
    </form>
  );
}
