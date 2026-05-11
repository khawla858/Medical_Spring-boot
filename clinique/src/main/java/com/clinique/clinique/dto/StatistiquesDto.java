package com.clinique.clinique.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatistiquesDto {
    private long totalPatients;
    private long totalMedecins;
    private long totalRendezVous;
    private long rendezVousConfirmes;
    private long rendezVousEnAttente;
    private long rendezVousAnnules;
    private double chiffreAffaire;
    private double tauxOccupation;
}