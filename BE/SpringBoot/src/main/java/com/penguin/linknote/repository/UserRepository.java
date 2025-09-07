package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Notebook;
import com.penguin.linknote.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(@NotEmpty String email);

    List<User> findAllByUsername(@NotEmpty String title);

    @Query("SELECT nb FROM Notebook nb WHERE nb.user.id = :userId")
    List<Notebook> findAllById(UUID userId);
}
