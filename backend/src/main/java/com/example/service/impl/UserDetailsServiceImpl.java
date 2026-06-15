package com.example.service.impl;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserEntity user = userRepository.findByUsernameWithRoles(username)
                                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

                Set<String> roles = new HashSet<>(user.getRoleSet());

                List<GrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority(role))
                                .collect(Collectors.toList());
                return new User(
                                user.getUsername(),
                                user.getPassword(),
                                authorities);
        }
}
