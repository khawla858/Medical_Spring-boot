package com.clinique.clinique.service.observer;


import com.clinique.clinique.model.Utilisateur;

public interface Subscriber {  // PATTERN OBSERVER - interface abonné
    void update(String eventType, Utilisateur user, String message);
    String getCanal();
}