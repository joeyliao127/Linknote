package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.penguin.linknote.entity.User;

public interface UserRepository {
    Optional<User> get(UUID id);

    User create(User user);

    User update(User user);

    void delete(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAllByUsername(String title);

    Optional<User> findByEmailAndPassword(String email, String password);
}
