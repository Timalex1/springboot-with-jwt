package com.thumbex.api.service;

import java.util.Collection;
import java.util.Collections;

import com.thumbex.api.domain.User;
import com.thumbex.api.repository.UserRepo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User prodosUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with this name found"));
        UserDetails user = new org.springframework.security.core.userdetails.User(username, prodosUser.getPassword(),
                getAuthority(prodosUser.getRole()));
        return user;
    }

    private Collection<? extends GrantedAuthority> getAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

}
