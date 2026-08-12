package com.contestpulse.service;

import com.contestpulse.dto.CreateUserRequest;
import com.contestpulse.dto.UserResponse;
import com.contestpulse.model.User;
import com.contestpulse.repository.UserRepository;
import com.contestpulse.exception.EmailAlreadyRegisteredException;
import com.contestpulse.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new EmailAlreadyRegisteredException(request.email());
        });

        User user = new User();
        user.setEmail(request.email());

        // Only override the entity's default timezone ("Asia/Kolkata") if the
        // caller actually sent a non-blank one.
        if (StringUtils.hasText(request.timezone())) {
            user.setTimezone(request.timezone());
        }

        User saved = userRepository.save(user);
        log.info("Created user id={} email={}", saved.getId(), saved.getEmail());

        return toResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getTimezone(),
                user.getTelegramChatId(),
                user.getWhatsappNumber()
        );
    }
}