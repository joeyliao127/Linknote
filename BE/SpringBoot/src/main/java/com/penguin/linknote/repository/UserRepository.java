package com.penguin.linknote.repository;

import com.penguin.linknote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
