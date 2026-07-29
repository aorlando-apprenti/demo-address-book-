import { describe, it, expect, vi, afterEach } from 'vitest';
import { cleanup, render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ContactManagement } from './ContactManagement';
import { contactsApi, ApiRequestError } from '../api/client';

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client');
  return {
    ...actual,
    contactsApi: {
      list: vi.fn(),
      search: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      remove: vi.fn(),
    },
  };
});

const sampleContact = {
  id: 1,
  name: 'Alice Smith',
  addressLine1: '1 Elm St',
  addressLine2: '',
  city: 'Springfield',
  state: 'IL',
  zipCode: '62701',
  telephoneNumber: '555-0100',
  email: 'alice@example.com',
  createdAt: '',
  updatedAt: '',
};

describe('ContactManagement', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('loads and displays contacts on mount', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);

    render(<ContactManagement token="tok" />);

    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());
    expect(contactsApi.list).toHaveBeenCalledWith('tok');
  });

  it('shows an alert when loading contacts fails', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockRejectedValue(new ApiRequestError(500, 'Server error'));

    render(<ContactManagement token="tok" />);

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Server error'));
  });

  it('adds a new contact and shows a success message', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([]);
    (contactsApi.create as ReturnType<typeof vi.fn>).mockResolvedValue(sampleContact);

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(contactsApi.list).toHaveBeenCalled());

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Alice Smith' } });
    fireEvent.change(screen.getByLabelText('Address Line 1'), { target: { value: '1 Elm St' } });
    fireEvent.change(screen.getByLabelText('Telephone Number'), { target: { value: '555-0100' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'alice@example.com' } });
    fireEvent.click(screen.getByRole('button', { name: 'Add Contact' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('Contact added successfully.'),
    );
    expect(contactsApi.create).toHaveBeenCalledWith(
      {
        name: 'Alice Smith',
        addressLine1: '1 Elm St',
        addressLine2: '',
        city: '',
        state: '',
        zipCode: '',
        telephoneNumber: '555-0100',
        email: 'alice@example.com',
      },
      'tok',
    );
  });

  it('shows an alert when adding a contact fails', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([]);
    (contactsApi.create as ReturnType<typeof vi.fn>).mockRejectedValue(
      new ApiRequestError(400, 'Name is required'),
    );

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(contactsApi.list).toHaveBeenCalled());

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'X' } });
    fireEvent.click(screen.getByRole('button', { name: 'Add Contact' }));

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Name is required'));
  });

  it('populates the form and updates an existing contact', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);
    (contactsApi.update as ReturnType<typeof vi.fn>).mockResolvedValue({ ...sampleContact, name: 'Alice Updated' });

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());

    fireEvent.click(screen.getByRole('button', { name: 'Edit' }));
    expect(screen.getByRole('heading', { name: 'Edit Contact' })).toBeInTheDocument();
    expect(screen.getByLabelText('Name')).toHaveValue('Alice Smith');

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Alice Updated' } });
    fireEvent.click(screen.getByRole('button', { name: 'Update Contact' }));

    await waitFor(() =>
      expect(screen.getByRole('status')).toHaveTextContent('Contact updated successfully.'),
    );
    expect(contactsApi.update).toHaveBeenCalledWith(
      1,
      {
        name: 'Alice Updated',
        addressLine1: '1 Elm St',
        addressLine2: '',
        city: 'Springfield',
        state: 'IL',
        zipCode: '62701',
        telephoneNumber: '555-0100',
        email: 'alice@example.com',
      },
      'tok',
    );
  });

  it('cancels an in-progress edit', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());

    fireEvent.click(screen.getByRole('button', { name: 'Edit' }));
    fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

    expect(screen.getByRole('heading', { name: 'Add Contact' })).toBeInTheDocument();
    expect(screen.getByLabelText('Name')).toHaveValue('');
  });

  it('deletes a contact', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>)
      .mockResolvedValueOnce([sampleContact])
      .mockResolvedValueOnce([]);
    (contactsApi.remove as ReturnType<typeof vi.fn>).mockResolvedValue({ message: 'Contact deleted successfully.' });

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());

    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(screen.queryByText('Alice Smith')).not.toBeInTheDocument());
    expect(contactsApi.remove).toHaveBeenCalledWith(1, 'tok');
  });

  it('shows an alert when deleting a contact fails', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);
    (contactsApi.remove as ReturnType<typeof vi.fn>).mockRejectedValue(new ApiRequestError(404, 'Resource not found'));

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());

    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Resource not found'));
  });

  it('searches contacts across name, address, phone, and email', async () => {
    (contactsApi.list as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);
    (contactsApi.search as ReturnType<typeof vi.fn>).mockResolvedValue([sampleContact]);

    render(<ContactManagement token="tok" />);
    await waitFor(() => expect(screen.getByText('Alice Smith')).toBeInTheDocument());

    fireEvent.change(screen.getByLabelText('Search Contacts'), { target: { value: 'elm' } });
    fireEvent.click(screen.getByRole('button', { name: 'Search' }));

    await waitFor(() => expect(contactsApi.search).toHaveBeenCalledWith('elm', 'tok'));
  });
});
