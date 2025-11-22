package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;
import com.penguin.linknote.domain.user.exception.BadCredentialsException;
import com.penguin.linknote.domain.user.exception.EmailAlreadyExistException;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        return null;
    }

    @Override
    public UserDTO getUserByName(String username) {
        List<User> userList = userRepository.findAllByUsername(username);
        if (userList.isEmpty()) {
            return null;
        } else {
            return UserDTO.fromEntity(userList.getFirst());
        }
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<NotebookDTO> getNotebooksByUserId(UUID userId) {
        return NotebookDTO.fromEntityList(userRepository.findAllById(userId));
    }

    @Override
    public UserDTO create(UserCreateCommand userCreateCommand) {
        Optional<User> existUser = userRepository.findByEmail(userCreateCommand.getEmail());

        if(existUser.isEmpty()) throw new EmailAlreadyExistException("Email already exist");

        // TODO: hash password with salt
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userCreateCommand.getEmail());
        user.setUsername(userCreateCommand.getUsername());
        user.setPassword(userCreateCommand.getPassword());
        user.setUserStatusId(1);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User newUser = userRepository.save(user);

        return UserDTO.fromEntity(newUser);
    }

    @Override
    public UserDTO updateUser(UserCreateCommand userCreateCommand) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UUID userId) {
        return null;
    }

    @Override
    public UserDTO verifyUser(UserSignInCommand command) {
        Optional<User> existUser = userRepository.findByEmailAndPassword(command.getEmail(), command.getPassword());
        if(existUser.isEmpty()) {
            throw new BadCredentialsException("Email or password is incorrect");
        }
        return UserDTO.fromEntity(existUser.get());
    }

    @Override
    public boolean existsById(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return !user.isEmpty();
    }
}
