package com.thumbex.api.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thumbex.api.dto.AccountCredentials;
import com.thumbex.api.repository.UserRepo;
import com.thumbex.api.security.JwtAuthService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtAuthService authService;
    private final UserRepo userRepo;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth/login")
    public void signIn(@RequestBody AccountCredentials credentials) {

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

            List list = new ArrayList<>();
            list.add(this.userRepo.findByUsername(credentials.getUsername()).orElseThrow( () -> 
                new UsernameNotFoundException("Username " + credentials.getUsername() + "not found")).getRole());

            String token = authService.createToken(credentials.getUsername(), list);

            Map model = new HashMap<>();
            model.put("username", credentials.getUsername());
            model.put("token", token);

        }

    catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
    }

}
