package com.shark.linkedInProject.userService.service;

import com.shark.linkedInProject.userService.dto.LoginRequestDto;
import com.shark.linkedInProject.userService.dto.SignupRequestDto;
import com.shark.linkedInProject.userService.dto.UserDto;
import com.shark.linkedInProject.userService.entity.User;
import com.shark.linkedInProject.userService.event.UserCreatedEvent;
import com.shark.linkedInProject.userService.exception.BadRequestException;
import com.shark.linkedInProject.userService.exception.ResourceNotFoundException;
import com.shark.linkedInProject.userService.repository.UserRepository;
import com.shark.linkedInProject.userService.utils.BCryptEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate;


    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}", signupRequestDto.getEmail());
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if (exists) {
            throw new BadRequestException("User already exists: " + signupRequestDto.getEmail());
        }

        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(BCryptEncoder.hash(signupRequestDto.getPassword()));
        user = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();

        userCreatedEventKafkaTemplate.send("user_created_topic", userCreatedEvent);

        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Login for a user with email: {}", loginRequestDto.getEmail());
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new BadRequestException("Incorrect email or password"));

        boolean isPasswordMatch = BCryptEncoder.match(loginRequestDto.getPassword(), user.getPassword());

        if (!isPasswordMatch) {
            throw new BadRequestException("Incorrect email or password");
        }

        return jwtService.generateAccessToken(user);
    }
}
