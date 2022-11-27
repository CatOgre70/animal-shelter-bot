package dev.pro.animalshelterbot.controller;


import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation (
            summary = "get information about the user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "information about the user from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))

                            )
                    )
            },
            tags = "User"
    )

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable Long id) {
        User user = userService.findUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    @Operation (
            summary = "get users by parameters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "information about the user from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))

                            )
                    )
            },
            tags = "User"
    )
    @GetMapping
    public ResponseEntity<Collection<User>> getUser(@Parameter(description = "first name User", example = "Aleksandr")@RequestParam  (required = false) String firstName,
                                                    @Parameter(description = "second name User", example = "Tsygulev")@RequestParam  (required = false) String secondName,
                                                    @Parameter(description = "nickname User", example = "MaloyTS")@RequestParam  (required = false) String nickName,
                                                    @Parameter(description = "chatId User", example = "1221")@RequestParam  (required = true) Long chatId) {
        if (firstName !=null && !firstName.isBlank()) {
            return ResponseEntity.ok(userService.findByFirstName(firstName));
        }
        if (secondName !=null && !secondName.isBlank()) {
            return ResponseEntity.ok(userService.findBySecondName(secondName));
        }
        if (nickName !=null && !nickName.isBlank()) {
            return ResponseEntity.ok(userService.findByNickName(nickName));
        }
        if (chatId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getAllUser());
    }
    @Operation (
            summary = "user creation",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return created user",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))

                            )
                    )
            },tags = "User"

    )

    @PostMapping // с помощью hidden = false в сваггере не отображается
    public User createUser(@Parameter(hidden = false) @RequestBody User user) {
        return userService.addUser(user);
    }

    @Operation (
            summary = "editing user parameters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "returns an user with modified parameters",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))
                            )
                    )
            },
            tags = "User"
    )

    @PutMapping
    public ResponseEntity<User> editUser(@RequestBody User user) {
        User user1 = userService.editUser(user);
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "deleting an user from the db",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))
                            )
                    )
            },
            tags = "User"
    )

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}