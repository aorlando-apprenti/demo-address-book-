import { useState, type FormEvent } from 'react';
import { adminApi, ApiRequestError } from '../api/client';

export interface AdminUserManagementProps {
  token: string;
}

export function AdminUserManagement({ token }: AdminUserManagementProps) {
  const [email, setEmail] = useState('');
  const [addressLine1, setAddressLine1] = useState('');
  const [addressLine2, setAddressLine2] = useState('');
  const [city, setCity] = useState('');
  const [state, setState] = useState('');
  const [zipCode, setZipCode] = useState('');
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
      const response = await adminApi.createUser(
        { email, addressLine1, addressLine2, city, state, zipCode, telephoneNumber },
        token,
      );
      setCreateResult(`User ${response.user.email} created. Temporary password: ${response.temporaryPassword}`);
      setEmail('');
      setAddressLine1('');
      setAddressLine2('');
      setCity('');
      setState('');
      setZipCode('');
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

        <label htmlFor="admin-new-address-line1">Address Line 1</label>
        <input
          id="admin-new-address-line1"
          type="text"
          value={addressLine1}
          onChange={(e) => setAddressLine1(e.target.value)}
          required
        />

        <label htmlFor="admin-new-address-line2">Address Line 2</label>
        <input
          id="admin-new-address-line2"
          type="text"
          value={addressLine2}
          onChange={(e) => setAddressLine2(e.target.value)}
        />

        <label htmlFor="admin-new-city">City</label>
        <input
          id="admin-new-city"
          type="text"
          value={city}
          onChange={(e) => setCity(e.target.value)}
          required
        />

        <label htmlFor="admin-new-state">State</label>
        <input
          id="admin-new-state"
          type="text"
          maxLength={2}
          placeholder="e.g. CA"
          value={state}
          onChange={(e) => setState(e.target.value.toUpperCase())}
          required
        />

        <label htmlFor="admin-new-zip">ZIP Code</label>
        <input
          id="admin-new-zip"
          type="text"
          placeholder="e.g. 94103 or 94103-1234"
          value={zipCode}
          onChange={(e) => setZipCode(e.target.value)}
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
