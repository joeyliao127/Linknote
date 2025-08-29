package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>, QuerydslPredicateExecutor<Tag> {
    List<Tag> findByUserId(UUID userId);
}
