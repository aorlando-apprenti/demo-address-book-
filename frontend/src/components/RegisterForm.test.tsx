import { describe, it, expect, vi, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor } from '@testing-library/react';
import { RegisterForm } from './RegisterForm';
import { authApi, ApiRequestError } from '../api/client';

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client');
  return {
    ...actual,
    authApi: { register: vi.fn(), login: vi.fn(), changePassword: vi.fn() },
  };
});

function fillForm() {
  fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'a@b.com' } });
  fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password1' } });
  fireEvent.change(screen.getByLabelText('Address Line 1'), { target: { value: '1 Elm St' } });
  fireEvent.change(screen.getByLabelText('City'), { target: { value: 'Springfield' } });
  fireEvent.change(screen.getByLabelText('State'), { target: { value: 'IL' } });
  fireEvent.change(screen.getByLabelText('ZIP Code'), { target: { value: '62701' } });
  fireEvent.change(screen.getByLabelText('Telephone Number'), { target: { value: '555-0100' } });
}

describe('RegisterForm', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('submits the payload and shows a success message', async () => {
    (authApi.register as ReturnType<typeof vi.fn>).mockResolvedValue({ id: 1, email: 'a@b.com' });
    const onRegistered = vi.fn();

    render(<RegisterForm onRegistered={onRegistered} />);
    fillForm();
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));

    await waitFor(() => expect(screen.getByRole('status')).toBeInTheDocument());

    expect(authApi.register).toHaveBeenCalledWith({
      email: 'a@b.com',
      password: 'password1',
      addressLine1: '1 Elm St',
      addressLine2: '',
      city: 'Springfield',
      state: 'IL',
      zipCode: '62701',
      telephoneNumber: '555-0100',
    });
    expect(onRegistered).toHaveBeenCalledTimes(1);
  });

  it('shows an error message when registration fails', async () => {
    (authApi.register as ReturnType<typeof vi.fn>).mockRejectedValue(
      new ApiRequestError(409, 'Email already exists'),
    );

    render(<RegisterForm />);
    fillForm();
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Email already exists'));
  });
});
