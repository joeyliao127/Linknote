package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface NotebookRepository extends JpaRepository<Notebook, UUID>, QuerydslPredicateExecutor<Notebook> {
}
