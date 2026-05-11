package com.clinique.clinique.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "plannings")
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Column(name = "jour_semaine", nullable = false)
    private String jourSemaine;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(nullable = false)
    private Boolean actif = true;

    // Constructeur par défaut
    public Planning() {}

    // Constructeur avec paramètres
    public Planning(Medecin medecin, String jourSemaine, LocalTime heureDebut, LocalTime heureFin, Boolean actif) {
        this.medecin = medecin;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.actif = actif;
    }

    // ============= GETTERS =============
    public Long getId() { return id; }
    public Medecin getMedecin() { return medecin; }
    public String getJourSemaine() { return jourSemaine; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public Boolean getActif() { return actif; }

    // ============= SETTERS =============
    public void setId(Long id) { this.id = id; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public void setActif(Boolean actif) { this.actif = actif; }
}