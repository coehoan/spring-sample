package org.example.awstest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.awstest.dto.UserLoginDto;
import org.example.awstest.dto.UserModifyDto;
import org.example.awstest.dto.UserSignupDto;
import org.example.awstest.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/signup")
    public void signup(@RequestBody UserSignupDto dto) {
        userService.register(dto);
    }

    @PostMapping("/login")
    public void login(@RequestBody UserLoginDto dto, HttpServletRequest request, HttpServletResponse response) {
        userService.login(dto);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

        Authentication auth = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }

    @PostMapping("/modify")
    public void modify(@RequestBody UserModifyDto dto) {
        if ("info".equals(dto.getType())) {
            userService.modifyUserInfo(dto);
        } else {
            userService.modifyUserPassword(dto);
        }
    }
}
