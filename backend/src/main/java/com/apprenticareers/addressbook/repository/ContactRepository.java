package com.apprenticareers.addressbook.repository;

import com.apprenticareers.addressbook.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByOwnerUserId(Long ownerUserId);

    Optional<Contact> findByIdAndOwnerUserId(Long id, Long ownerUserId);

    /**
     * FR-10: multi-field search (name, address, telephoneNumber, email),
     * always scoped to the owning user.
     */
    @Query("SELECT c FROM Contact c WHERE c.ownerUserId = :ownerUserId AND ("
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) OR "
            + "LOWER(c.address) LIKE LOWER(CONCAT('%', :term, '%')) OR "
            + "LOWER(c.telephoneNumber) LIKE LOWER(CONCAT('%', :term, '%')) OR "
            + "LOWER(c.email) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Contact> searchByOwnerUserIdAndTerm(@Param("ownerUserId") Long ownerUserId, @Param("term") String term);

    /**
     * FR-05 cascade support: removes all contacts owned by a user being deleted.
     */
    void deleteByOwnerUserId(Long ownerUserId);
}
