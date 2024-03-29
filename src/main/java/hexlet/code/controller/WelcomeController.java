package hexlet.code.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/welcome")
public final class WelcomeController {

    @GetMapping
    public @ResponseBody String getGreeting() {
        return "Welcome to Spring";
    }

}
