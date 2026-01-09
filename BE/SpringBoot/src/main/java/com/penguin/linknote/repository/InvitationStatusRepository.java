package com.penguin.linknote.repository;

import java.util.Optional;

import com.penguin.linknote.entity.InvitationStatusCode;

public interface InvitationStatusRepository {
    Optional<InvitationStatusCode> findByTitle(String title);
}
