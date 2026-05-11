package com.clinique.clinique.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedecinDto {
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private Double tarifBase;
    private String email;
    private String bio;
    
    public MedecinDto(com.clinique.clinique.model.Medecin medecin) {
        this.id = medecin.getId();
        this.specialite = medecin.getSpecialite();
        this.tarifBase = medecin.getTarifBase();
        this.bio = medecin.getBio();
        
        if (medecin.getUtilisateur() != null) {
            this.nom = medecin.getUtilisateur().getNom();
            this.prenom = medecin.getUtilisateur().getPrenom();
            this.email = medecin.getUtilisateur().getEmail();
        }
    }
}