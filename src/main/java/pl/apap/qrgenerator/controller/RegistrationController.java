package pl.apap.qrgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.apap.qrgenerator.service.UserService;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("confirm-password") String confirmPassword) {
        if (password.equals(confirmPassword)) {
            userService.registerUser(username, password);
            return "redirect:/login";
        } else {
            // Możesz dodać logikę obsługi błędów, np. przekierowanie z komunikatem
            return "register?error";
        }
    }
}
