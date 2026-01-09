package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;

import com.penguin.linknote.entity.Resources;

public interface ResourcesRepository {
    List<Resources> index(Integer limit);

    Optional<Resources> get(Integer id);

    Resources create(Resources resource);

    Resources update(Resources resource);

    void delete(Integer id);
}
