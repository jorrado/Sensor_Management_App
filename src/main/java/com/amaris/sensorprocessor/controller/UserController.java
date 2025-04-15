package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.entity.User;
import com.amaris.sensorprocessor.exception.ProblemeUsersException;
import com.amaris.sensorprocessor.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
//        model.asMap().remove("user");
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("loggedUsername", loggedUsername);
        return "manageUsers";
    }

    @PostMapping("/manage-users/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.save(user);
        } catch (ProblemeUsersException e) {
            redirectAttributes.addFlashAttribute("error", "User already exists!");
            return "redirect:/manage-users";
        }
        redirectAttributes.addFlashAttribute("error", null);
        return "redirect:/manage-users";
    }

    @PostMapping("/manage-users/delete/{username}")
    public String deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return "redirect:/manage-users";
    }

    @GetMapping("/manage-users/edit/{username}")
    public String editUser(@PathVariable String username, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        User user = userService.searchUserByUsername(username);
        model.addAttribute("user", user);
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("loggedUsername", loggedUsername);
        return "manageUsers";
    }

    @PostMapping("/manage-users/edit")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.update(user);
        return "redirect:/manage-users";
    }

}
