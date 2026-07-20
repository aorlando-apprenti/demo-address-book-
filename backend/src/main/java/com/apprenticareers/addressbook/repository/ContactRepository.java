package com.apprenticareers.addressbook.repository;

import com.apprenticareers.addressbook.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByOwnerUserId(Long ownerUserId);
    List<Contact> findByOwnerUserIdAndNameContaining(Long ownerUserId, String namePattern);
}
