package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vocabulary.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        log.info("GET /login");
        return "login";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String username, @RequestParam String password) {
        log.info("POST /register username={} password=******", username);
        boolean created = userService.saveIfAbsent(username, password);
        model.addAttribute("created", created);
        return "login";
    }

    @GetMapping("/")
    public String helloWorld(Model model) {
        log.info("GET /");
        return "index";
    }
}
