import { describe, it, expect, vi, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AdminUserManagement } from './AdminUserManagement';
import { adminApi, ApiRequestError } from '../api/client';

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client');
  return {
    ...actual,
    adminApi: { createUser: vi.fn(), removeUser: vi.fn(), resetPassword: vi.fn() },
  };
});

describe('AdminUserManagement', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('creates a user and shows the generated temporary password', async () => {
    (adminApi.createUser as ReturnType<typeof vi.fn>).mockResolvedValue({
      user: {
        id: 3,
        email: 'new@example.com',
        addressLine1: 'addr',
        city: 'Springfield',
        state: 'IL',
        zipCode: '62701',
        telephoneNumber: '1',
        role: 'USER',
        createdAt: '',
      },
      temporaryPassword: 'Temp123',
    });

    render(<AdminUserManagement token="tok" />);
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'new@example.com' } });
    fireEvent.change(screen.getByLabelText('Address Line 1'), { target: { value: 'addr' } });
    fireEvent.change(screen.getByLabelText('City'), { target: { value: 'Springfield' } });
    fireEvent.change(screen.getByLabelText('State'), { target: { value: 'IL' } });
    fireEvent.change(screen.getByLabelText('ZIP Code'), { target: { value: '62701' } });
    fireEvent.change(screen.getByLabelText('Telephone Number'), { target: { value: '1' } });
    fireEvent.click(screen.getByRole('button', { name: 'Add User' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('Temporary password: Temp123'),
    );
  });

  it('shows an error message when user creation fails', async () => {
    (adminApi.createUser as ReturnType<typeof vi.fn>).mockRejectedValue(
      new ApiRequestError(409, 'Email already exists'),
    );

    render(<AdminUserManagement token="tok" />);
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'dup@example.com' } });
    fireEvent.change(screen.getByLabelText('Address Line 1'), { target: { value: 'addr' } });
    fireEvent.change(screen.getByLabelText('City'), { target: { value: 'Springfield' } });
    fireEvent.change(screen.getByLabelText('State'), { target: { value: 'IL' } });
    fireEvent.change(screen.getByLabelText('ZIP Code'), { target: { value: '62701' } });
    fireEvent.change(screen.getByLabelText('Telephone Number'), { target: { value: '1' } });
    fireEvent.click(screen.getByRole('button', { name: 'Add User' }));

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Email already exists'));
  });

  it('removes a user by id', async () => {
    (adminApi.removeUser as ReturnType<typeof vi.fn>).mockResolvedValue({ message: 'User removed successfully.' });

    render(<AdminUserManagement token="tok" />);
    fireEvent.change(screen.getByLabelText('User ID'), { target: { value: '5' } });
    fireEvent.click(screen.getByRole('button', { name: 'Remove User' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('User removed successfully.'),
    );
    expect(adminApi.removeUser).toHaveBeenCalledWith(5, 'tok');
  });

  it('resets a password by id', async () => {
    (adminApi.resetPassword as ReturnType<typeof vi.fn>).mockResolvedValue({
      userId: 5,
      email: 'target@example.com',
      newPassword: 'NewPass1',
    });

    render(<AdminUserManagement token="tok" />);
    fireEvent.change(screen.getByLabelText('User ID'), { target: { value: '5' } });
    fireEvent.click(screen.getByRole('button', { name: 'Reset Password' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('New password for target@example.com: NewPass1'),
    );
  });
});
