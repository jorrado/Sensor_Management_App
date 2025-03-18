package com.amaris.sensorprocessor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Redirige vers la page de connexion (login.html).
     * @return la vue "login" pour afficher la page de connexion.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Redirige vers la page d'accueil (home.html).
     * @return la vue "home" pour afficher la page d'accueil.
     */
    @GetMapping("/home")
    public String homePage() { return "home"; }

}
