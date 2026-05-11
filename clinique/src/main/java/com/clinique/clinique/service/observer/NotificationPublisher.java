package com.clinique.clinique.service.observer;

import com.clinique.clinique.model.Notification;
import com.clinique.clinique.model.Utilisateur;
import com.clinique.clinique.repository.NotificationRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationPublisher {  // PATTERN OBSERVER - publisher
    
    private final NotificationRepository notificationRepository;
    private final List<Subscriber> subscribers = new ArrayList<>();
    
    // Constructeur explicite
    public NotificationPublisher(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
    
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
    
    public void notifier(String eventType, Utilisateur user, String message) {
        // Sauvegarder en base (sans Builder)
        Notification notification = new Notification();
        notification.setUtilisateur(user);
        notification.setTypeEvent(eventType);
        notification.setMessage(message);
        notification.setSujet(eventType);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setLu(false);
        notification.setCanal("IN_APP");
        
        notificationRepository.save(notification);
        
        // Notifier tous les abonnés
        for (Subscriber subscriber : subscribers) {
            subscriber.update(eventType, user, message);
        }
        
        System.out.println("Notification envoyée à " + user.getEmail() + ": " + message);
    }
}