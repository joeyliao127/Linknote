package com.penguin.linknote.service;

import java.util.List;
import java.util.UUID;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String username);
    UserDTO getUserByEmail(String email);
    List<NotebookDTO> getNotebooksByUserId(UUID userId);
    UserDTO create(UserCreateCommand userCreateCommand);
    UserDTO updateUser(UserCreateCommand userCreateCommand);
    UserDTO deleteUser(UUID userId);
    UserDTO verifyUser(UserSignInCommand userSignInCommand);
}
