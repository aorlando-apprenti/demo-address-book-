package com.apprenticareers.addressbook.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A private contact record owned by exactly one {@link User} (FR-07–FR-11).
 * {@code ownerUserId} is stored as a plain FK column (not a JPA association)
 * to keep the Contact Management bounded context decoupled from Identity &amp;
 * Access internals, per Architecture §6 ("Contact Management never queries
 * User internals beyond the ID"). Referential cleanup on user removal is
 * handled explicitly in {@code AdminUserService} via
 * {@code ContactRepository#deleteByOwnerUserId}.
 */
@Entity
@Table(name = "contacts", indexes = {
        @Index(name = "idx_contacts_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_contacts_owner_user_id_name", columnList = "owner_user_id, name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(nullable = false)
    private String name;

    private String address;

    private String telephoneNumber;

    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
