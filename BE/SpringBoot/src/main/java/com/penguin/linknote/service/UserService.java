package com.penguin.linknote.service;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCommand;
import com.penguin.linknote.domain.user.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO getUserById(UUID userId);
    UserDTO getUserByName(String username);
    UserDTO getUserByEmail(String email);
    List<NotebookDTO> getNotebooksByUserId(UUID userId);
    UserDTO createUser(UserCommand userCommand);
    UserDTO updateUser(UserCommand userCommand);
    UserDTO deleteUser(UUID userId);
}
