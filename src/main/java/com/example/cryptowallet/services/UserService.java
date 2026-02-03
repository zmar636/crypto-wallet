package com.example.cryptowallet.services;

import com.example.cryptowallet.entities.User;
import com.example.cryptowallet.exceptions.UserAlreadyExistsException;
import com.example.cryptowallet.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(String userEmail) {
        Optional<User> existentUser = userRepository.findByEmail(userEmail);

        if (existentUser.isPresent()) {
            throw new UserAlreadyExistsException(userEmail);
        }

        User user = new User();
        user.setEmail(userEmail);
        userRepository.save(user);
    }
}
