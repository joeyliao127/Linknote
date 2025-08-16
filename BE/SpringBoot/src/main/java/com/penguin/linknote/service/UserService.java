package com.penguin.linknote.service;

import com.penguin.linknote.domain.user.UserCommand;
import com.penguin.linknote.domain.user.UserDTO;

import java.util.UUID;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String userName);
    UserDTO getUserByEmail(String email);
    UserDTO createUser(UserCommand userCommand);
    UserDTO updateUser(UserCommand userCommand);
    UserDTO deleteUser(UUID userId);
}
