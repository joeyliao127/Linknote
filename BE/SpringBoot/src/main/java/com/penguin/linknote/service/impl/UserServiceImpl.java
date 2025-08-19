package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.exception.user.EmailAlreadyExistException;
import com.penguin.linknote.domain.user.UserCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        return null;
    }

    @Override
    public UserDTO getUserByName(String userName) {
        return null;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserDTO createUser(UserCommand userCommand) {
        User existUser = userRepository.findByEmail(userCommand.getEmail());

        if(existUser != null) {
            throw new EmailAlreadyExistException("Email already exist");
        }

        // TODO: hash password with salt
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userCommand.getEmail());
        user.setUsername(userCommand.getUsername());
        user.setPassword(userCommand.getPassword());
        user.setUserStatusId(1);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User newUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO();

        userDTO.setId(newUser.getId());
        userDTO.setUsername(newUser.getUsername());
        userDTO.setEmail(newUser.getEmail());
        userDTO.setCreatedAt(newUser.getCreatedAt());
        userDTO.setUpdatedAt(newUser.getUpdatedAt());
        userDTO.setEnabled(newUser.getUserStatusId() == 1);

        return userDTO;
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    public UserDTO updateUser(UserCommand userCommand) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UUID userId) {
        return null;
    }
}
