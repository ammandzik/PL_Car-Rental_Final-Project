package pl.coderslab.carrental.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.carrental.model.CurrentUser;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        return "Welcome to Car rental application!";
    }

    @GetMapping("/logged")
    public String admin(@AuthenticationPrincipal CurrentUser customUser) {

        var entityUser = customUser.getUser();
        return String.format("Hello %s %s !", entityUser.getName(), entityUser.getSurname());
    }
}
