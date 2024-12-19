package com.tekup.storage_system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tekup.storage_system.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/home")
    public String homePage(Authentication authentication, Model model) {
        model.addAttribute("user", userService.getUserByUsername(authentication.getName()));
        model.addAttribute("activeUrl", "/home");
        model.addAttribute("pageTitle", "Accueil");
        model.addAttribute("content", "home");
        return "base";
    }


}
