package com.clinique.clinique.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "index";
    }
    
    @GetMapping("/patient")
    public String patientDashboard() {
        return "patient";
    }
    
    @GetMapping("/medecin")
    public String medecinDashboard() {
        return "medecin";
    }
    
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }
}