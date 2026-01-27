package com.example.mealTracker.controller;


import com.example.mealTracker.common.JwtProvider;
import com.example.mealTracker.dto.*;
import com.example.mealTracker.mapper.UserMapper;
import com.example.mealTracker.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest vo, HttpServletResponse response) {
        try{
            Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(vo.getEmail(), vo.getPassword())
            );

            String email = auth.getName();

            long userId = userMapper.findByEmail(email).getId();

            String access = jwtProvider.createAccessToken(userId, vo.getEmail());
            String refresh = jwtProvider.createRefreshToken(userId);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refresh)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(14 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(Map.of("accessToken", access));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/me")
    public ResponseEntity<MealTrackerUserResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }

    @PutMapping("/target")
    public ResponseEntity<MeResponse> target(Authentication authentication, @RequestBody UpdateTargetsResponse vo) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateTargets(userId, vo));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest vo) {
        userService.sign(vo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!jwtProvider.isType(refreshToken, "refresh")) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        long userId = jwtProvider.getUserId(refreshToken);

        String access = jwtProvider.createAccessToken(userId, "test@test.com");
        return Map.of("accessToken", access);
    }
}
