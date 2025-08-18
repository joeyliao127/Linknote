package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NotebookRepository extends JpaRepository<Notebook, Long>, QuerydslPredicateExecutor<Notebook> {
}
