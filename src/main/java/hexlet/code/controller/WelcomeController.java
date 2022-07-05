package hexlet.code.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/welcome")
public final class WelcomeController {

    @GetMapping
    public String getGreeting() {
        return "Welcome to Spring";
    }

}
