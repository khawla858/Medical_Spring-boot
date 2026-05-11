package com.clinique.clinique.dto;


import com.clinique.clinique.model.RendezVous;
import com.clinique.clinique.model.StatutRendezVous;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RendezVousDto {
    private Long id;
    private String patientNom;
    private String medecinNom;
    private String medecinSpecialite;
    private LocalDateTime dateRdv;
    private String dateFormatee;
    private String motif;
    private StatutRendezVous statut;
    private String statutLibelle;
    private Double montant;
    
    public RendezVousDto(RendezVous rdv) {
        this.id = rdv.getId();
        this.patientNom = rdv.getPatient().getUtilisateur().getNomComplet();
        this.medecinNom = rdv.getMedecin().getNomComplet();
        this.medecinSpecialite = rdv.getMedecin().getSpecialite();
        this.dateRdv = rdv.getDateRdv();
        this.dateFormatee = rdv.getDateRdv().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.motif = rdv.getMotif();
        this.statut = rdv.getStatut();
        this.statutLibelle = rdv.getStatut().getLibelle();
        this.montant = rdv.getMontantFinal();
    }
}