package com.penguin.linknote.service;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String username);
    UserDTO getUserByEmail(String email);
    List<NotebookDTO> getNotebooksByUserId(UUID userId);
    UserDTO createUser(UserCreateCommand userCreateCommand);
    UserDTO updateUser(UserCreateCommand userCreateCommand);
    UserDTO deleteUser(UUID userId);
    UserDTO verifyUser(UserSignInCommand userSignInCommand);
}
