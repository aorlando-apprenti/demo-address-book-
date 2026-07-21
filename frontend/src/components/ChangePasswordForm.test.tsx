import { describe, it, expect, vi, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ChangePasswordForm } from './ChangePasswordForm';
import { authApi, ApiRequestError } from '../api/client';

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client');
  return {
    ...actual,
    authApi: { register: vi.fn(), login: vi.fn(), changePassword: vi.fn() },
  };
});

describe('ChangePasswordForm', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('submits old/new password with the bearer token and shows success', async () => {
    (authApi.changePassword as ReturnType<typeof vi.fn>).mockResolvedValue({ message: 'ok' });

    render(<ChangePasswordForm token="tok123" />);
    fireEvent.change(screen.getByLabelText('Current Password'), { target: { value: 'old' } });
    fireEvent.change(screen.getByLabelText('New Password'), { target: { value: 'newpw' } });
    fireEvent.click(screen.getByRole('button', { name: 'Update Password' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('Password updated successfully.'),
    );
    expect(authApi.changePassword).toHaveBeenCalledWith({ oldPassword: 'old', newPassword: 'newpw' }, 'tok123');
  });

  it('shows an error message when the old password is incorrect', async () => {
    (authApi.changePassword as ReturnType<typeof vi.fn>).mockRejectedValue(
      new ApiRequestError(401, 'Current password is incorrect'),
    );

    render(<ChangePasswordForm token="tok123" />);
    fireEvent.change(screen.getByLabelText('Current Password'), { target: { value: 'wrong' } });
    fireEvent.change(screen.getByLabelText('New Password'), { target: { value: 'newpw' } });
    fireEvent.click(screen.getByRole('button', { name: 'Update Password' }));

    await waitFor(() =>
      expect(screen.getByRole('alert')).toHaveTextContent('Current password is incorrect'),
    );
  });
});
