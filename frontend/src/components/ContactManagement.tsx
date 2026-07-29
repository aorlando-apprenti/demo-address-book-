import { useEffect, useState, type FormEvent } from 'react';
import { contactsApi, ApiRequestError, type ContactPayload, type ContactResponse } from '../api/client';

export interface ContactManagementProps {
  token: string;
}

const EMPTY_FORM: ContactPayload = {
  name: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  zipCode: '',
  telephoneNumber: '',
  email: '',
};

export function ContactManagement({ token }: ContactManagementProps) {
  const [contacts, setContacts] = useState<ContactResponse[]>([]);
  const [listError, setListError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState('');

  const [form, setForm] = useState<ContactPayload>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [formStatus, setFormStatus] = useState<string | null>(null);

  const [editingId, setEditingId] = useState<number | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  async function loadContacts(term: string) {
    try {
      const results = term.trim() ? await contactsApi.search(term, token) : await contactsApi.list(token);
      setContacts(results);
      setListError(null);
    } catch (err) {
      setListError(err instanceof ApiRequestError ? err.message : 'Failed to load contacts');
    }
  }

  useEffect(() => {
    loadContacts('');
    // Only load once on mount; subsequent loads are explicitly triggered.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleSearchSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await loadContacts(searchTerm);
  }

  async function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError(null);
    setFormStatus(null);
    try {
      if (editingId !== null) {
        await contactsApi.update(editingId, form, token);
        setFormStatus('Contact updated successfully.');
      } else {
        await contactsApi.create(form, token);
        setFormStatus('Contact added successfully.');
      }
      setForm(EMPTY_FORM);
      setEditingId(null);
      await loadContacts(searchTerm);
    } catch (err) {
      setFormError(err instanceof ApiRequestError ? err.message : 'Failed to save contact');
    }
  }

  function handleEditClick(contact: ContactResponse) {
    setEditingId(contact.id);
    setForm({
      name: contact.name ?? '',
      addressLine1: contact.addressLine1 ?? '',
      addressLine2: contact.addressLine2 ?? '',
      city: contact.city ?? '',
      state: contact.state ?? '',
      zipCode: contact.zipCode ?? '',
      telephoneNumber: contact.telephoneNumber ?? '',
      email: contact.email ?? '',
    });
    setFormError(null);
    setFormStatus(null);
  }

  function handleCancelEdit() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setFormStatus(null);
  }

  async function handleDelete(id: number) {
    setActionError(null);
    try {
      await contactsApi.remove(id, token);
      if (editingId === id) {
        handleCancelEdit();
      }
      await loadContacts(searchTerm);
    } catch (err) {
      setActionError(err instanceof ApiRequestError ? err.message : 'Failed to delete contact');
    }
  }

  return (
    <section aria-label="contact-management">
      <h2>My Contacts</h2>

      <form onSubmit={handleSearchSubmit} aria-label="contact-search-form">
        <label htmlFor="contact-search">Search Contacts</label>
        <input
          id="contact-search"
          type="text"
          placeholder="Search by name, address, phone, or email"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>

      {listError && <p role="alert">{listError}</p>}
      {actionError && <p role="alert">{actionError}</p>}

      <ul aria-label="contact-list">
        {contacts.map((contact) => (
          <li key={contact.id}>
            <span>{contact.name}</span>{' '}
            <span>
              {[contact.addressLine1, contact.addressLine2, contact.city, contact.state, contact.zipCode]
                .filter(Boolean)
                .join(', ')}
            </span>{' '}
            <span>{contact.telephoneNumber}</span> <span>{contact.email}</span>
            <button type="button" onClick={() => handleEditClick(contact)}>
              Edit
            </button>
            <button type="button" onClick={() => handleDelete(contact.id)}>
              Delete
            </button>
          </li>
        ))}
      </ul>

      <form onSubmit={handleFormSubmit} aria-label="contact-form">
        <h3>{editingId !== null ? 'Edit Contact' : 'Add Contact'}</h3>

        <label htmlFor="contact-name">Name</label>
        <input
          id="contact-name"
          type="text"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          required
        />

        <label htmlFor="contact-address-line1">Address Line 1</label>
        <input
          id="contact-address-line1"
          type="text"
          value={form.addressLine1}
          onChange={(e) => setForm({ ...form, addressLine1: e.target.value })}
        />

        <label htmlFor="contact-address-line2">Address Line 2</label>
        <input
          id="contact-address-line2"
          type="text"
          value={form.addressLine2}
          onChange={(e) => setForm({ ...form, addressLine2: e.target.value })}
        />

        <label htmlFor="contact-city">City</label>
        <input
          id="contact-city"
          type="text"
          value={form.city}
          onChange={(e) => setForm({ ...form, city: e.target.value })}
        />

        <label htmlFor="contact-state">State</label>
        <input
          id="contact-state"
          type="text"
          maxLength={2}
          placeholder="e.g. CA"
          value={form.state}
          onChange={(e) => setForm({ ...form, state: e.target.value.toUpperCase() })}
        />

        <label htmlFor="contact-zip">ZIP Code</label>
        <input
          id="contact-zip"
          type="text"
          placeholder="e.g. 94103 or 94103-1234"
          value={form.zipCode}
          onChange={(e) => setForm({ ...form, zipCode: e.target.value })}
        />

        <label htmlFor="contact-phone">Telephone Number</label>
        <input
          id="contact-phone"
          type="text"
          value={form.telephoneNumber}
          onChange={(e) => setForm({ ...form, telephoneNumber: e.target.value })}
        />

        <label htmlFor="contact-email">Email</label>
        <input
          id="contact-email"
          type="email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />

        <button type="submit">{editingId !== null ? 'Update Contact' : 'Add Contact'}</button>
        {editingId !== null && (
          <button type="button" onClick={handleCancelEdit}>
            Cancel
          </button>
        )}

        {formError && <p role="alert">{formError}</p>}
        {formStatus && <p role="status">{formStatus}</p>}
      </form>
    </section>
  );
}
