package com.penguin.linknote.controller.user;

import com.penguin.linknote.domain.user.UserCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final String path;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.path = "/users";
    }

    @GetMapping
    public ResponseEntity<String> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body("get all users");

    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCommand userCommand) {
        UserDTO userDTO = userService.createUser(userCommand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + path + "/" + userDTO.getId()))
                .body(userDTO);
    }
}
