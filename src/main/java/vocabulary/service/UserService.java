package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.entity.User;
import vocabulary.repository.UserRepository;

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
}
