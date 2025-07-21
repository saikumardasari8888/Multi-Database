package com.multi.database.service;


import com.multi.database.configuration.Database;
import com.multi.database.configuration.DatabaseContextHolder;
import com.multi.database.model.User;
import com.multi.database.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Database("database1")
    @Transactional(readOnly = true)
    public List<User> getAllUsersFromDatabase1() {
        log.info("Fetching all users from database1");
        return userRepository.findAll();
    }

    @Database("database2")
    @Transactional(readOnly = true)
    public List<User> getAllUsersFromDatabase2() {
        log.info("Fetching all users from database2");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsersFromDatabase(String databaseName) {
        try {
            DatabaseContextHolder.setCurrentDatabase(databaseName);
            log.info("Fetching all users from database: {}", databaseName);
            return userRepository.findAll();
        } finally {
            DatabaseContextHolder.clear();
        }
    }

    @Database("database1")
    @Transactional
    public User createUserInDatabase1(User user) {
        log.info("Creating user in database1: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User createUserInDatabase(User user, String databaseName) {
        try {
            DatabaseContextHolder.setCurrentDatabase(databaseName);
            log.info("Creating user in database {}: {}", databaseName, user.getUsername());
            return userRepository.save(user);
        } finally {
            DatabaseContextHolder.clear();
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsernameInDatabase(String username, String databaseName) {
        try {
            DatabaseContextHolder.setCurrentDatabase(databaseName);
            log.info("Finding user {} in database: {}", username, databaseName);
            return userRepository.findByUsername(username);
        } finally {
            DatabaseContextHolder.clear();
        }
    }

    @Transactional(readOnly = true)
    public long getUserCountInDatabase(String databaseName) {
        try {
            DatabaseContextHolder.setCurrentDatabase(databaseName);
            log.info("Counting users in database: {}", databaseName);
            return userRepository.countByActiveTrue();
        } finally {
            DatabaseContextHolder.clear();
        }
    }
}