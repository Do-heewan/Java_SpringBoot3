package me.noh.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.noh.springbootdeveloper.domain.User;
import me.noh.springbootdeveloper.dto.AddUserRequest;
import me.noh.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 패스워드 암호화
                .build()).getId();
    }

    // 전달받은 유저 id로 유저를 검색해서 전달하는 메서드
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("not found"));
    }
}