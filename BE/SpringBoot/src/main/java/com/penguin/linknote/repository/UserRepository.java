package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.penguin.linknote.entity.User;

import jakarta.validation.constraints.NotEmpty;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(@NotEmpty String email);

    List<User> findAllByUsername(@NotEmpty String title);

    Optional<User> findByEmailAndPassword(String email, String password);
}
