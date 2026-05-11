package com.clinique.clinique.model;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(length = 20, unique = true)
    private String cin;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "groupe_sanguin", length = 5)
    private String groupeSanguin;

    @Column(length = 255)
    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String antecedents;

    @Column(name = "numero_dossier", unique = true, length = 30)
    private String numeroDossier;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "patient")
    private List<RendezVous> rendezVous = new ArrayList<>();

    public Patient() {}

    public Patient(Utilisateur utilisateur, String numeroDossier) {
        this.utilisateur = utilisateur;
        this.numeroDossier = numeroDossier;
        this.dateCreation = LocalDateTime.now();
    }

    // PROTOTYPE pattern
    @Override
    public Patient clone() {
        try {
            Patient clone = (Patient) super.clone();
            clone.setId(null);
            clone.setNumeroDossier(null);
            clone.setDateCreation(LocalDateTime.now());
            clone.setRendezVous(new ArrayList<>());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone impossible", e);
        }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getAntecedents() { return antecedents; }
    public void setAntecedents(String antecedents) { this.antecedents = antecedents; }
    public String getNumeroDossier() { return numeroDossier; }
    public void setNumeroDossier(String numeroDossier) { this.numeroDossier = numeroDossier; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public List<RendezVous> getRendezVous() { return rendezVous; }
    public void setRendezVous(List<RendezVous> rendezVous) { this.rendezVous = rendezVous; }
    
}