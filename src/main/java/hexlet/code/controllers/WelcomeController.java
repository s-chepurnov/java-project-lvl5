package hexlet.code.controllers;

import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @Autowired
    private Rollbar rollbar;

    @GetMapping("/welcome")
    public String welcome() {
        rollbar.debug("Here is some debug message");
        rollbar.debug("Rollbar works");
        return "Welcome to Spring Boot";
    }

}
