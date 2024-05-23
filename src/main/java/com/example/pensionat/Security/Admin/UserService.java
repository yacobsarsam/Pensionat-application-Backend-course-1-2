package com.example.pensionat.Security.Admin;

import com.example.pensionat.Security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 )); // 1 hour expiry
        tokenRepository.save(myToken);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        User user = passToken.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(passToken);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void sendPasswordResetEmail(String email) {
        User user = findUserByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);
            String resetUrl = "http://localhost:8080/reset-password?token=" + token;
            emailService.sendEmail(email, "Password Reset Request", "To reset your password, click the link below:\n" + resetUrl);
        }
    }
}
