package com.clinique.clinique.service.observer;


import com.clinique.clinique.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class EmailSubscriber implements Subscriber {
    
    @Override
    public void update(String eventType, Utilisateur user, String message) {
        // Simulation d'envoi d'email
        System.out.println("📧 EMAIL envoyé à " + user.getEmail() + " [" + eventType + "]: " + message);
    }
    
    @Override
    public String getCanal() {
        return "EMAIL";
    }
}