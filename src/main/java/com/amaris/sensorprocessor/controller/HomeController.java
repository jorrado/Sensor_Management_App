package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private LoginService loginService;

    /**
     * Redirige vers la page de connexion (login.html).
     * @return la vue "login" pour afficher la page de connexion.
     */
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    /**
     * Traite le formulaire de connexion.
     * @param username le nom d'utilisateur
     * @param password le mot de passe
     * @param model le modèle pour ajouter un attribut d'erreur
     * @return la vue "home" si authentifié, sinon "login" avec un message d'erreur
     */
    @PostMapping("/login")
    public String homePage(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        boolean isAuthenticated = false;
        try {
            isAuthenticated = loginService.authenticate(username, password);
        } catch(Exception e) {
            model.addAttribute("error", "Incorrect username or password");
            return "login";
        }

        if (isAuthenticated) {
            return "home";
        } else {
            model.addAttribute("error", "Incorrect username or password");
            return "login";
        }
    }
}
