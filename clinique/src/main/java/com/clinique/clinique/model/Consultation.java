package com.clinique.clinique.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", nullable = false, unique = true)
    private RendezVous rendezVous;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes = 30;

    @Column(name = "date_consultation")
    private LocalDateTime dateConsultation = LocalDateTime.now();

    // Constructeur par défaut
    public Consultation() {}

    // Constructeur avec paramètres
    public Consultation(RendezVous rendezVous, String diagnostic, String prescription, 
                        String observations, Integer dureeMinutes, LocalDateTime dateConsultation) {
        this.rendezVous = rendezVous;
        this.diagnostic = diagnostic;
        this.prescription = prescription;
        this.observations = observations;
        this.dureeMinutes = dureeMinutes;
        this.dateConsultation = dateConsultation;
    }

    // ============= GETTERS =============
    public Long getId() { return id; }
    public RendezVous getRendezVous() { return rendezVous; }
    public String getDiagnostic() { return diagnostic; }
    public String getPrescription() { return prescription; }
    public String getObservations() { return observations; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public LocalDateTime getDateConsultation() { return dateConsultation; }

    // ============= SETTERS =============
    public void setId(Long id) { this.id = id; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public void setDateConsultation(LocalDateTime dateConsultation) { this.dateConsultation = dateConsultation; }
}