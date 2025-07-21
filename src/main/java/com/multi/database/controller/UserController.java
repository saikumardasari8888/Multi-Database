package com.multi.database.controller;


import com.multi.database.model.User;
import com.multi.database.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/database1")
    public ResponseEntity<List<User>> getUsersFromDatabase1() {
        List<User> users = userService.getAllUsersFromDatabase1();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/database2")
    public ResponseEntity<List<User>> getUsersFromDatabase2() {
        List<User> users = userService.getAllUsersFromDatabase2();
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersFromDatabase(
            @RequestParam(defaultValue = "database1") String database) {
        List<User> users = userService.getAllUsersFromDatabase(database);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/database1")
    public ResponseEntity<User> createUserInDatabase1(@RequestBody User user) {
        User createdUser = userService.createUserInDatabase1(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping
    public ResponseEntity<User> createUserInDatabase(
            @RequestBody User user,
            @RequestParam(defaultValue = "database1") String database) {
        User createdUser = userService.createUserInDatabase(user, database);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/search")
    public ResponseEntity<User> findUserByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "database1") String database) {
        Optional<User> user = userService.findUserByUsernameInDatabase(username, database);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount(
            @RequestParam(defaultValue = "database1") String database) {
        long count = userService.getUserCountInDatabase(database);
        return ResponseEntity.ok(count);
    }
}