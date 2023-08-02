package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.dto.UserDto;
import vocabulary.entity.User;
import vocabulary.entity.enums.Voice;
import vocabulary.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean saveIfAbsent(String username, String password) {
        if (userRepository.findById(username).isPresent()) {
            return false;
        }
        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build());
        return true;
    }

    @Transactional(readOnly = true)
    public UserDto getUser(String username) {
        return userRepository.findById(username).map(UserDto::from).orElse(UserDto.emptyForUser(username));
    }

    @Transactional
    public boolean changePassword(String username, String passwordOld, String passwordNew) {
        return userRepository.findById(username)
                .filter(user -> passwordEncoder.matches(passwordOld, user.getPassword()))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(passwordNew));
                    return user;
                })
                .map(userRepository::save)
                .isPresent();
    }

    @Transactional
    public void setVoiceEnabled(String username, boolean voiceEnabled) {
        userRepository.findById(username).map(user -> {
            user.setVoiceEnabled(voiceEnabled);
            return user;
        }).ifPresent(userRepository::save);
    }

    @Transactional
    public void setVoiceCard(String username, Voice voiceCard) {
        userRepository.findById(username).map(user -> {
            user.setVoiceCard(voiceCard);
            return user;
        }).ifPresent(userRepository::save);
    }

    @Transactional
    public void setVoiceCharLeft(String username, Voice voiceChatLeft) {
        userRepository.findById(username).map(user -> {
            user.setVoiceChatLeft(voiceChatLeft);
            return user;
        }).ifPresent(userRepository::save);
    }

    @Transactional
    public void setVoiceChatRight(String username, Voice voiceChatRight) {
        userRepository.findById(username).map(user -> {
            user.setVoiceChatRight(voiceChatRight);
            return user;
        }).ifPresent(userRepository::save);
    }

    @Transactional
    public void setVoiceRate(String username, BigDecimal voiceRate) {
        userRepository.findById(username).map(user -> {
            user.setVoiceRate(voiceRate);
            return user;
        }).ifPresent(userRepository::save);
    }

    @Transactional
    public void setVoiceVolume(String username, BigDecimal voiceVolume) {
        userRepository.findById(username).map(user -> {
            user.setVoiceVolume(voiceVolume);
            return user;
        }).ifPresent(userRepository::save);
    }
}
