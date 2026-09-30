package com.example.securitylab.controller;

import com.example.securitylab.dto.AuthRequest;
import com.example.securitylab.dto.AuthResponse;
import com.example.securitylab.dto.DataPayload;
import com.example.securitylab.entity.Post;
import com.example.securitylab.repository.PostRepository;
import com.example.securitylab.security.JwtUtil;
import com.example.securitylab.util.HtmlSanitizer;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PostRepository postRepository;

    public DataController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, PostRepository postRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.postRepository = postRepository;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(request.username());
        return ResponseEntity.ok(new AuthResponse(token, HtmlSanitizer.sanitize(request.username()), jwtUtil.getExpirationMs()));
    }

    @GetMapping("/api/data")
    public List<DataPayload> getData(@AuthenticationPrincipal UserDetails userDetails) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream()
            .map(post -> new DataPayload(
                post.getId(),
                HtmlSanitizer.sanitize(post.getTitle()),
                HtmlSanitizer.sanitize(post.getContent()),
                HtmlSanitizer.sanitize(post.getAuthor().getUsername()),
                post.getCreatedAt().toString()))
            .toList();
    }

    @GetMapping("/api/profile")
    public Map<String, String> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return Map.of(
            "username", HtmlSanitizer.sanitize(userDetails.getUsername()),
            "message", HtmlSanitizer.sanitize("You are authenticated and can access protected resources.")
        );
    }
}
