package com.penguin.linknote.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.domain.auth.AuthClaim;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.user.UserCreateCommand;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.domain.user.UserSignInCommand;
import com.penguin.linknote.service.JWTService;
import com.penguin.linknote.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;
    private final String path;

    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.path = "/users";
    }

    @GetMapping
    public ResponseEntity<UserDTO> index(@RequestParam String username) {
        UserDTO userDTOList = userService.getUserByName(username);
        return ResponseEntity.ok(userDTOList);

    }

    @GetMapping("/notebooks")
    public ResponseEntity<List<NotebookDTO>> getAllNotebooks(@RequestHeader(name = "Authorization") UUID userId) {
        return ResponseEntity.ok(userService.getNotebooksByUserId(userId));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDTO> create(@RequestBody @Valid UserCreateCommand userCreateCommand) {
        UserDTO userDTO = userService.create(userCreateCommand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + path + "/" + userDTO.getId()))
                .body(userDTO);
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody @Valid UserSignInCommand userCreateCommand) {
        UserDTO userDTO = userService.verifyUser(userCreateCommand);
        String token = jwtService.generateToken(userDTO.getId());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/token")
    public ResponseEntity<AuthClaim> signIn(@RequestHeader("Authorization") String authorization) {
        
        return ResponseEntity.ok(jwtService.parseBearerToken(authorization.toString()));
    }
}
