package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID>, QuerydslPredicateExecutor<Invitation> {
}
