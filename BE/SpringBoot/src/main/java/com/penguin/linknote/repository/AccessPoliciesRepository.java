package com.penguin.linknote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.penguin.linknote.entity.AccessPolicies;

public interface AccessPoliciesRepository extends JpaRepository<AccessPolicies, Integer> {
    // Sample code
    // Page<Invitation> findByInviterId(UUID userId, Pageable pageable);
    // Page<Invitation> findByInviteeId(UUID userId, Pageable pageable);
    // Optional<Invitation> findByInviterIdAndInviteeId(UUID userId, UUID inviteeId);
    // Optional<Invitation> findByIdAndInviteeId(UUID id, UUID inviteeId);
}
