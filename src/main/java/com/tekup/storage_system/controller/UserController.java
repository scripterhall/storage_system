package com.tekup.storage_system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tekup.storage_system.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/me")
    public String getUserDetailsAfterLogin(Authentication authentication, Model model) {
        model.addAttribute("user", userService.getUserByUsername(authentication.getName()));

        model.addAttribute("pageTitle", "profile");
        model.addAttribute("content", "/me/profile");
        return "base";
    }

    @RequestMapping("/settings")
    public String getUserDetailsForSettings(Authentication authentication, Model model) {
        model.addAttribute("user", userService.getUserByUsername(authentication.getName()));
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("content", "/me/settings");
        return "base";
    }

}
