import { useState, type FormEvent } from 'react';
import { adminApi, ApiRequestError } from '../api/client';

export interface AdminUserManagementProps {
  token: string;
}

export function AdminUserManagement({ token }: AdminUserManagementProps) {
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [telephoneNumber, setTelephoneNumber] = useState('');
  const [createError, setCreateError] = useState<string | null>(null);
  const [createResult, setCreateResult] = useState<string | null>(null);

  const [targetId, setTargetId] = useState('');
  const [actionError, setActionError] = useState<string | null>(null);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setCreateError(null);
    setCreateResult(null);
    try {
      const response = await adminApi.createUser({ email, address, telephoneNumber }, token);
      setCreateResult(`User ${response.user.email} created. Temporary password: ${response.temporaryPassword}`);
      setEmail('');
      setAddress('');
      setTelephoneNumber('');
    } catch (err) {
      setCreateError(err instanceof ApiRequestError ? err.message : 'Failed to create user');
    }
  }

  async function handleRemove() {
    setActionError(null);
    setActionMessage(null);
    try {
      const response = await adminApi.removeUser(Number(targetId), token);
      setActionMessage(response.message);
    } catch (err) {
      setActionError(err instanceof ApiRequestError ? err.message : 'Failed to remove user');
    }
  }

  async function handleResetPassword() {
    setActionError(null);
    setActionMessage(null);
    try {
      const response = await adminApi.resetPassword(Number(targetId), token);
      setActionMessage(`New password for ${response.email}: ${response.newPassword}`);
    } catch (err) {
      setActionError(err instanceof ApiRequestError ? err.message : 'Failed to reset password');
    }
  }

  return (
    <section aria-label="admin-user-management">
      <h2>Admin: User Management</h2>

      <form onSubmit={handleCreate} aria-label="create-user-form">
        <h3>Add User</h3>

        <label htmlFor="admin-new-email">Email</label>
        <input
          id="admin-new-email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label htmlFor="admin-new-address">Address</label>
        <input
          id="admin-new-address"
          type="text"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          required
        />

        <label htmlFor="admin-new-phone">Telephone Number</label>
        <input
          id="admin-new-phone"
          type="text"
          value={telephoneNumber}
          onChange={(e) => setTelephoneNumber(e.target.value)}
          required
        />

        <button type="submit">Add User</button>

        {createError && <p role="alert">{createError}</p>}
        {createResult && <p role="status">{createResult}</p>}
      </form>

      <div aria-label="manage-user-section">
        <h3>Remove / Reset Password</h3>

        <label htmlFor="admin-target-id">User ID</label>
        <input
          id="admin-target-id"
          type="number"
          value={targetId}
          onChange={(e) => setTargetId(e.target.value)}
        />

        <button type="button" onClick={handleRemove}>
          Remove User
        </button>
        <button type="button" onClick={handleResetPassword}>
          Reset Password
        </button>

        {actionError && <p role="alert">{actionError}</p>}
        {actionMessage && <p role="status">{actionMessage}</p>}
      </div>
    </section>
  );
}
