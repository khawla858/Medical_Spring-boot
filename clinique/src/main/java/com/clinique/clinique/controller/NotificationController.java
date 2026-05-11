package com.clinique.clinique.controller;

import com.clinique.clinique.model.Notification;
import com.clinique.clinique.model.Utilisateur;
import com.clinique.clinique.repository.NotificationRepository;
import com.clinique.clinique.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationController(NotificationRepository notificationRepository,
                                  UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Récupérer les notifications de l'utilisateur connecté
    @GetMapping("/mes")
    public ResponseEntity<?> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            List<Notification> notifications = notificationRepository.findByUtilisateurIdOrderByDateEnvoiDesc(user.getId());
            long nonLues = notificationRepository.countByUtilisateurIdAndLu(user.getId(), false);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("nonLues", nonLues);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Marquer une notification comme lue
    @PostMapping("/{id}/lire")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
            notif.setLu(true);
            notificationRepository.save(notif);
            return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Marquer toutes les notifications comme lues
    @PostMapping("/lire-tout")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            List<Notification> notifications = notificationRepository.findByUtilisateurIdAndLu(user.getId(), false);
            for (Notification notif : notifications) {
                notif.setLu(true);
            }
            notificationRepository.saveAll(notifications);
            
            return ResponseEntity.ok(Map.of("message", "Toutes les notifications marquées comme lues"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}