package com.clinique.clinique.service.observer;


import com.clinique.clinique.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class SmsSubscriber implements Subscriber {
    
    @Override
    public void update(String eventType, Utilisateur user, String message) {
        // Simulation d'envoi SMS
        System.out.println("📱 SMS envoyé à " + user.getTelephone() + " [" + eventType + "]: " + message);
    }
    
    @Override
    public String getCanal() {
        return "SMS";
    }
}
