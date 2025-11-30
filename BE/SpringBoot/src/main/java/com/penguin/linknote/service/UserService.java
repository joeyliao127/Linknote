package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String username);
    UserDTO getUserByEmail(String email);
    UserDTO create(UserCreateCommand userCreateCommand);
    UserDTO updateUser(UserCreateCommand userCreateCommand);
    UserDTO deleteUser(UUID userId);
    UserDTO verifyUser(UserSignInCommand userSignInCommand);
    boolean existsById(UUID userId);
}
