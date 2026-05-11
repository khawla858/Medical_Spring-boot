package com.clinique.clinique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medecins")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"medecin", "patient", "password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 100)
    private String specialite;

    @Column(name = "numero_ordre", nullable = false, unique = true, length = 30)
    private String numeroOrdre;

    @Column(name = "tarif_base", nullable = false)
    private Double tarifBase = 150.0;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private List<Planning> plannings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "medecin")
    private List<RendezVous> rendezVous = new ArrayList<>();

    public Medecin() {}

    public Medecin(Utilisateur utilisateur, String specialite, String numeroOrdre, Double tarifBase) {
        this.utilisateur = utilisateur;
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.tarifBase = tarifBase;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }
    public Double getTarifBase() { return tarifBase; }
    public void setTarifBase(Double tarifBase) { this.tarifBase = tarifBase; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public List<Planning> getPlannings() { return plannings; }
    public void setPlannings(List<Planning> plannings) { this.plannings = plannings; }
    public List<RendezVous> getRendezVous() { return rendezVous; }
    public void setRendezVous(List<RendezVous> rendezVous) { this.rendezVous = rendezVous; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getNomComplet() {
        return utilisateur != null ? "Dr. " + utilisateur.getNomComplet() : "Dr. ?";
    }
}