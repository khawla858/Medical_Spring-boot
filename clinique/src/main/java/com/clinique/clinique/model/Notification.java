package com.clinique.clinique.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "type_event", length = 50, nullable = false)
    private String typeEvent;

    @Column(length = 10, nullable = false)
    private String canal = "IN_APP";

    @Column(length = 255)
    private String sujet;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean lu = false;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    // Constructeur par défaut
    public Notification() {}

    // ============= GETTERS =============
    public Long getId() { return id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public String getTypeEvent() { return typeEvent; }
    public String getCanal() { return canal; }
    public String getSujet() { return sujet; }
    public String getMessage() { return message; }
    public Boolean getLu() { return lu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }

    // ============= SETTERS =============
    public void setId(Long id) { this.id = id; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public void setTypeEvent(String typeEvent) { this.typeEvent = typeEvent; }
    public void setCanal(String canal) { this.canal = canal; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public void setMessage(String message) { this.message = message; }
    public void setLu(Boolean lu) { this.lu = lu; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
}