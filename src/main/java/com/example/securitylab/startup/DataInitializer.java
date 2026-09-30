package com.example.securitylab.startup;

import com.example.securitylab.entity.Post;
import com.example.securitylab.entity.User;
import com.example.securitylab.repository.PostRepository;
import com.example.securitylab.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", passwordEncoder.encode("admin123"));
            userRepository.save(admin);
        }

        User admin = userRepository.findByUsername("admin").orElseThrow();
        if (postRepository.count() == 0) {
            postRepository.save(new Post("Welcome", "This secured API can only be accessed with a valid JWT token.", admin));
            postRepository.save(new Post("Security note", "All database queries rely on parameterized access and all responses are HTML-escaped.", admin));
        }
    }
}
