package com.clinique.clinique.service.observer;


import com.clinique.clinique.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class InAppSubscriber implements Subscriber {
    
    @Override
    public void update(String eventType, Utilisateur user, String message) {
        // Notification in-app
        System.out.println("🔔 Notification in-app pour " + user.getNomComplet() + ": " + message);
    }
    
    @Override
    public String getCanal() {
        return "IN_APP";
    }
}