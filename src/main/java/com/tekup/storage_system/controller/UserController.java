package com.tekup.storage_system.controller;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tekup.storage_system.model.User;
import com.tekup.storage_system.service.UserService;
import com.tekup.storage_system.utils.PasswordChanger;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/me")
    public String getUserDetailsAfterLogin(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        String formattedCreatedAt = user.getCreatedAt() != null
                ? user.getCreatedAt().toLocalDate().toString()
                : "Date not available";
        model.addAttribute("formattedCreatedAt", formattedCreatedAt);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "profile");
        model.addAttribute("content", "/me/profile");
        StringBuilder fullName = new StringBuilder(user.getGivenName());
        fullName.append(" ").append(user.getFamilyName());
        model.addAttribute("fullName", fullName);
        return "base";
    }

    @RequestMapping("/settings")
    public String getUserDetailsForSettings(Authentication authentication, Model model) {
        model.addAttribute("user", userService.getUserByUsername(authentication.getName()));
        model.addAttribute("passwordChanger", new PasswordChanger());
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("content", "/me/settings");
        return "base";
    }

    @PostMapping(value = "/settings")
    public String updateProduct(@ModelAttribute User modifiedUser, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        if (!authentication.getName().equals(modifiedUser.getUsername())) {
            user.setUsername(modifiedUser.getUsername());
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            modifiedUser.getUsername(),
                            authentication.getCredentials(),
                            authentication.getAuthorities()));
        }
        user.setGivenName(modifiedUser.getGivenName());
        user.setFamilyName(modifiedUser.getFamilyName());
        user.setGenre(modifiedUser.getGenre());
        user.setEmail(modifiedUser.getEmail());
        userService.upadteMe(user);
        return "redirect:/me";
    }

    @PostMapping("/avatar")
    public String postMethodName(@RequestParam("profilePicture") MultipartFile profilePicture,
            Authentication authentication, Model model) {
        try {
            userService.changeProfilePicture(profilePicture, authentication.getName());
        } catch (IOException e) {
            model.addAttribute("error", "can\'t change profile picture ");
        }
        return "redirect:/me?pic=true";
    }

    @PostMapping("/cred")
    public String updatePassword(@ModelAttribute PasswordChanger passwordChanger, Authentication authentication) {
        if (!passwordChanger.isPasswordsMatch()) {
            return "redirect:/settings?error=new-password_and_password_confirm_not_match";
        }
        if (userService.changePassword(passwordChanger, authentication.getName()))
            return "redirect:/settings?success=password_updated";
        else
            return "redirect:/settings?error=wrong_password";
    }

    @GetMapping("/delete")
    public String deleteUser(Authentication authentication) {
        userService.delete(userService.getUserByUsername(authentication.getName()).getId());
        return "redirect:/auth/logout";
    }

    public User getCurrentUser(Authentication authentication) {
        return userService.getUserByUsername(authentication.getName());
    }
}
