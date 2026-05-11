package com.clinique.clinique.service.factory;

import com.clinique.clinique.model.Role;
import com.clinique.clinique.model.Utilisateur;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;

public class UtilisateurFactory {  // PATTERN FACTORY METHOD
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Méthode principale qui crée un utilisateur
    public static Utilisateur creerUtilisateur(String nom, String prenom, String email, 
                                                String password, Role role) {
        // Au lieu de Utilisateur.builder()...build()
        // On utilise le constructeur + setters
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(password));  // Mot de passe crypté
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        
        return utilisateur;
    }
    
    // Factory Method : crée spécifiquement un PATIENT
    public static Utilisateur creerPatient(String nom, String prenom, String email, String password) {
        return creerUtilisateur(nom, prenom, email, password, Role.PATIENT);
    }
    
    // Factory Method : crée spécifiquement un MEDECIN
    public static Utilisateur creerMedecin(String nom, String prenom, String email, String password) {
        return creerUtilisateur(nom, prenom, email, password, Role.DOCTOR);
    }
    
    // Factory Method : crée spécifiquement un ADMIN
    public static Utilisateur creerAdmin(String nom, String prenom, String email, String password) {
        return creerUtilisateur(nom, prenom, email, password, Role.ADMIN);
    }
}