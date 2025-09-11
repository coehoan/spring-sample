package org.example.awstest.controller;

import lombok.RequiredArgsConstructor;
import org.example.awstest.entity.User;
import org.example.awstest.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class FrontController {
    private final UserService userService;

    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal == null) {
            return "redirect:/login?expired";
        }

        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", user);
        return "mypage";
    }
}
