package org.example.awstest.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.awstest.dto.UserLoginDto;
import org.example.awstest.dto.UserModifyDto;
import org.example.awstest.dto.UserSignupDto;
import org.example.awstest.entity.User;
import org.example.awstest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void register(UserSignupDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .age(dto.getAge())
                .regDt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public void login(UserLoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void modifyUserInfo(UserModifyDto dto) {
        User user = userRepository.findById(dto.getId()).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
    }

    @Transactional
    public void modifyUserPassword(UserModifyDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자 정보를 찾을 수 없습니다."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles("USER")
                .build();
    }
}
