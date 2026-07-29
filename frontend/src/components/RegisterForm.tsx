import { useState, type FormEvent } from 'react';
import { authApi, ApiRequestError } from '../api/client';

export interface RegisterFormProps {
  onRegistered?: () => void;
}

export function RegisterForm({ onRegistered }: RegisterFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [addressLine1, setAddressLine1] = useState('');
  const [addressLine2, setAddressLine2] = useState('');
  const [city, setCity] = useState('');
  const [state, setState] = useState('');
  const [zipCode, setZipCode] = useState('');
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
      await authApi.register({ email, password, addressLine1, addressLine2, city, state, zipCode, telephoneNumber });
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

      <label htmlFor="register-address-line1">Address Line 1</label>
      <input
        id="register-address-line1"
        type="text"
        value={addressLine1}
        onChange={(e) => setAddressLine1(e.target.value)}
        required
      />

      <label htmlFor="register-address-line2">Address Line 2</label>
      <input
        id="register-address-line2"
        type="text"
        value={addressLine2}
        onChange={(e) => setAddressLine2(e.target.value)}
      />

      <label htmlFor="register-city">City</label>
      <input
        id="register-city"
        type="text"
        value={city}
        onChange={(e) => setCity(e.target.value)}
        required
      />

      <label htmlFor="register-state">State</label>
      <input
        id="register-state"
        type="text"
        maxLength={2}
        placeholder="e.g. CA"
        value={state}
        onChange={(e) => setState(e.target.value.toUpperCase())}
        required
      />

      <label htmlFor="register-zip">ZIP Code</label>
      <input
        id="register-zip"
        type="text"
        placeholder="e.g. 94103 or 94103-1234"
        value={zipCode}
        onChange={(e) => setZipCode(e.target.value)}
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
