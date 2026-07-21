import { useState, type FormEvent } from 'react';
import { authApi, ApiRequestError } from '../api/client';

export interface ChangePasswordFormProps {
  token: string;
}

export function ChangePasswordForm({ token }: ChangePasswordFormProps) {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setSuccess(false);
    setSubmitting(true);
    try {
      await authApi.changePassword({ oldPassword, newPassword }, token);
      setSuccess(true);
      setOldPassword('');
      setNewPassword('');
    } catch (err) {
      setError(err instanceof ApiRequestError ? err.message : 'Password change failed');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} aria-label="change-password-form">
      <h2>Change Password</h2>

      <label htmlFor="old-password">Current Password</label>
      <input
        id="old-password"
        type="password"
        value={oldPassword}
        onChange={(e) => setOldPassword(e.target.value)}
        required
      />

      <label htmlFor="new-password">New Password</label>
      <input
        id="new-password"
        type="password"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        required
      />

      <button type="submit" disabled={submitting}>
        Update Password
      </button>

      {error && <p role="alert">{error}</p>}
      {success && <p role="status">Password updated successfully.</p>}
    </form>
  );
}
