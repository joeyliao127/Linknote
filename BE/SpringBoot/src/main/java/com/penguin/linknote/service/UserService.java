package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.domain.user.UserChangePasswordCommand;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;
import com.penguin.linknote.domain.user.UserUpdateProfileCommand;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String username);
    UserDTO getUserByEmail(String email);
    UserDTO create(UserCreateCommand userCreateCommand);
    UserDTO updateProfile(UUID userId, UserUpdateProfileCommand cmd);
    void changePassword(UUID userId, UserChangePasswordCommand cmd);
    void deleteUser(UUID userId);
    UserDTO verifyUser(UserSignInCommand userSignInCommand);
    boolean existsById(UUID userId);
}
