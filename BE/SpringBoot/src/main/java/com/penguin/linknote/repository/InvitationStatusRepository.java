package com.penguin.linknote.repository;

import com.penguin.linknote.entity.InvitationStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationStatusRepository extends JpaRepository<InvitationStatusCode, Long> {
    InvitationStatusCode findByTitle(String title);
}
