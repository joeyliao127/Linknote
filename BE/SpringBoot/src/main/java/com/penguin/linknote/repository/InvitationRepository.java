package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID>, QuerydslPredicateExecutor<Invitation> {
    Page<Invitation> findByInviterId(UUID userId, Pageable pageable);
    Page<Invitation> findByInviteeId(UUID userId, Pageable pageable);
    Optional<Invitation> findByInviterIdAndInviteeId(UUID userId, UUID inviteeId);
    Optional<Invitation> findByIdAndInviteeId(UUID id, UUID inviteeId);
}
