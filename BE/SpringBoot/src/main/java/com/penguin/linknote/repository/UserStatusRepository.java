package com.penguin.linknote.repository;

import com.penguin.linknote.entity.UserStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatusCode, Long> {
}
