package com.clinique.clinique.dto;

import com.clinique.clinique.model.Patient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DossierDto {
    
    protected Long id;
    protected String nomComplet;
    protected String cin;
    protected LocalDate dateNaissance;
    protected String groupeSanguin;
    protected String allergies;
    protected String antecedents;
    protected String numeroDossier;
    protected String adresse;
    
    // Constructeur à partir d'un Patient
    public DossierDto(Patient patient) {
        if (patient != null) {
            this.id = patient.getId();
            if (patient.getUtilisateur() != null) {
                this.nomComplet = patient.getUtilisateur().getNomComplet();
            }
            this.cin = patient.getCin();
            this.dateNaissance = patient.getDateNaissance();
            this.groupeSanguin = patient.getGroupeSanguin();
            this.allergies = patient.getAllergies();
            this.antecedents = patient.getAntecedents();
            this.numeroDossier = patient.getNumeroDossier();
            this.adresse = patient.getAdresse();
        }
    }
    
    // Constructeur par défaut
    public DossierDto() {}
    
    // ============= GETTERS =============
    public Long getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public String getCin() { return cin; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public String getAllergies() { return allergies; }
    public String getAntecedents() { return antecedents; }
    public String getNumeroDossier() { return numeroDossier; }
    public String getAdresse() { return adresse; }
    
    // Formater la date de naissance
    public String getDateNaissanceFormatee() {
        if (dateNaissance == null) return "Non renseignée";
        return dateNaissance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    // ============= SETTERS =============
    public void setId(Long id) { this.id = id; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public void setCin(String cin) { this.cin = cin; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setAntecedents(String antecedents) { this.antecedents = antecedents; }
    public void setNumeroDossier(String numeroDossier) { this.numeroDossier = numeroDossier; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}