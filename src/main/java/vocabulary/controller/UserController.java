package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.ChangePasswordDto;
import vocabulary.controller.dto.UserDto;
import vocabulary.entity.User;
import vocabulary.entity.enums.Voice;
import vocabulary.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/api/user")
    public ResponseEntity<UserDto> user(Principal principal) {
        log.info("GET /api/user");
        UserDto user = userService.getUser(principal.getName());
        log.info("GET /api/user Response: {}", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/user/password/change")
    public ResponseEntity<Boolean> passwordChange(Principal principal, @RequestBody ChangePasswordDto dto) {
        log.info("GET /api/user/password/change");
        if (!Objects.equals(dto.getPasswordNew(), dto.getPasswordConfirm())) {
            return ResponseEntity.ok(false);
        }
        boolean success = userService.changePassword(principal.getName(), dto.getPasswordOld(), dto.getPasswordNew());
        return ResponseEntity.ok(success);
    }

    @PostMapping(value = "/api/user/voice/enabled")
    public ResponseEntity<String> voiceEnabled(Principal principal, @RequestBody String voiceEnabled) {
        log.info("POST /api/user/voice/enabled {}", voiceEnabled);
        userService.setVoiceEnabled(principal.getName(), Boolean.parseBoolean(voiceEnabled));
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/api/user/voice/card")
    public ResponseEntity<String> voiceCard(Principal principal, @RequestBody String voiceCard) {
        log.info("POST /api/user/voice/card {}", voiceCard);
        userService.setVoiceCard(principal.getName(), Voice.valueOf(voiceCard));
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/api/user/voice/chat/left")
    public ResponseEntity<String> voiceChatLeft(Principal principal, @RequestBody String voiceChatLeft) {
        log.info("POST /api/user/voice/chat/left {}", voiceChatLeft);
        userService.setVoiceCharLeft(principal.getName(), Voice.valueOf(voiceChatLeft));
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/api/user/voice/chat/right")
    public ResponseEntity<String> voiceChatRight(Principal principal, @RequestBody String voiceChatRight) {
        log.info("POST /api/user/voice/chat/right {}", voiceChatRight);
        userService.setVoiceChatRight(principal.getName(), Voice.valueOf(voiceChatRight));
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/api/user/voice/rate")
    public ResponseEntity<String> voiceRate(Principal principal, @RequestBody String voiceRate) {
        log.info("POST /api/user/voice/rate {}", voiceRate);
        userService.setVoiceRate(principal.getName(), new BigDecimal(voiceRate));
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/api/user/voice/volume")
    public ResponseEntity<String> voiceVolume(Principal principal, @RequestBody String voiceVolume) {
        log.info("POST /api/user/voice/volume {}", voiceVolume);
        userService.setVoiceVolume(principal.getName(), new BigDecimal(voiceVolume));
        return ResponseEntity.ok("OK");
    }
}
