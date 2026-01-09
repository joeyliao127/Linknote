package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;

import com.penguin.linknote.entity.UserStatusCode;

public interface UserStatusRepository {
    List<UserStatusCode> index(Integer limit);

    Optional<UserStatusCode> get(Long id);

    UserStatusCode create(UserStatusCode status);

    UserStatusCode update(UserStatusCode status);

    void delete(Long id);
}
