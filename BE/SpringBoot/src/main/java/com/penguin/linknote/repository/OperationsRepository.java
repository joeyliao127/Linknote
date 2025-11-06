package com.penguin.linknote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.penguin.linknote.entity.Operations;

// TODO: 更改 Jpa ID 類別
public interface OperationsRepository extends JpaRepository<Operations, Integer> {
    // TODO: Sample code
    // Page<Invitation> findByInviterId(UUID userId, Pageable pageable);
    // Page<Invitation> findByInviteeId(UUID userId, Pageable pageable);
    // Optional<Invitation> findByInviterIdAndInviteeId(UUID userId, UUID inviteeId);
    // Optional<Invitation> findByIdAndInviteeId(UUID id, UUID inviteeId);
}
