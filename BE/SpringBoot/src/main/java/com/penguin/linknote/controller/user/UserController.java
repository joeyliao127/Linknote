package com.penguin.linknote.controller.user;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;
import com.penguin.linknote.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<UserDTO> index(@RequestParam String username) {
        UserDTO userDTOList = userService.getUserByName(username);
        return ResponseEntity.ok(userDTOList);

    }

    @GetMapping("/notebooks")
    public ResponseEntity<List<NotebookDTO>> getAllNotebooks(@RequestParam(name = "Authorization") UUID userId) {
        return ResponseEntity.ok(userService.getNotebooksByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateCommand userCreateCommand) {
        UserDTO userDTO = userService.createUser(userCreateCommand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + path + "/" + userDTO.getId()))
                .body(userDTO);
    }

    @PostMapping("/signIn")
    public ResponseEntity<UserDTO> signIn(@RequestBody @Valid UserSignInCommand userCreateCommand) {
        UserDTO userDTO = userService.verifyUser(userCreateCommand);
        return ResponseEntity.ok(userDTO);
    }
}
