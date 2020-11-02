package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final HashService hashService;

    public UserService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public boolean isUsernameAvailable(String userName) {
        return userMapper.getUser(userName) == null;
    }

    public User getUser(String username) {
        return userMapper.getUser(username);
    }

    public Integer createUser(User user) {
        // Fills a salt with random numbers
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hashes password for storing in database
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);

        // Returns the userId attribute of the created user
        return userMapper.insertUser(new User(null, user.getUserName(),
                encodedSalt, hashedPassword, user.getFirstName(), user.getLastName()));
    }
}
