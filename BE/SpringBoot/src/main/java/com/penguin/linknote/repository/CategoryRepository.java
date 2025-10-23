package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Category;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 更改 Jpa ID 類別
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // TODO: Sample code
    // Page<Invitation> findByInviterId(UUID userId, Pageable pageable);
    // Page<Invitation> findByInviteeId(UUID userId, Pageable pageable);
    // Optional<Invitation> findByInviterIdAndInviteeId(UUID userId, UUID inviteeId);
    // Optional<Invitation> findByIdAndInviteeId(UUID id, UUID inviteeId);
}
