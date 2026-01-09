package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;

import com.penguin.linknote.entity.Operations;

public interface OperationsRepository {
    List<Operations> index(Integer limit);

    Optional<Operations> get(Integer id);

    Operations create(Operations operation);

    Operations update(Operations operation);

    void delete(Integer id);
}
