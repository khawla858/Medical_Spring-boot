package com.clinique.clinique.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Column(name = "date_rdv", nullable = false)
    private LocalDateTime dateRdv;

    @Column(length = 255)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRendezVous statut = StatutRendezVous.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_facturation", nullable = false)
    private TypeFacturation typeFacturation = TypeFacturation.PRIVE;

    @Column(name = "montant_final")
    private Double montantFinal;

    @Column(name = "notes_medecin", columnDefinition = "TEXT")
    private String notesMedecin;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_maj")
    private LocalDateTime dateMaj;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
    private Consultation consultation;

    // Constructeur par défaut
    public RendezVous() {}

    // Constructeur avec paramètres
    public RendezVous(Patient patient, Medecin medecin, LocalDateTime dateRdv, 
                      String motif, TypeFacturation typeFacturation, Double montantFinal) {
        this.patient = patient;
        this.medecin = medecin;
        this.dateRdv = dateRdv;
        this.motif = motif;
        this.typeFacturation = typeFacturation;
        this.montantFinal = montantFinal;
        this.statut = StatutRendezVous.PENDING;
        this.dateCreation = LocalDateTime.now();
    }

    // ============= GETTERS =============
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Medecin getMedecin() { return medecin; }
    public LocalDateTime getDateRdv() { return dateRdv; }
    public String getMotif() { return motif; }
    public StatutRendezVous getStatut() { return statut; }
    public TypeFacturation getTypeFacturation() { return typeFacturation; }
    public Double getMontantFinal() { return montantFinal; }
    public String getNotesMedecin() { return notesMedecin; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateMaj() { return dateMaj; }
    public Consultation getConsultation() { return consultation; }

    // ============= SETTERS =============
    public void setId(Long id) { this.id = id; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
    public void setDateRdv(LocalDateTime dateRdv) { this.dateRdv = dateRdv; }
    public void setMotif(String motif) { this.motif = motif; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }
    public void setTypeFacturation(TypeFacturation typeFacturation) { this.typeFacturation = typeFacturation; }
    public void setMontantFinal(Double montantFinal) { this.montantFinal = montantFinal; }
    public void setNotesMedecin(String notesMedecin) { this.notesMedecin = notesMedecin; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public void setDateMaj(LocalDateTime dateMaj) { this.dateMaj = dateMaj; }
    public void setConsultation(Consultation consultation) { 
        this.consultation = consultation; 
    }

    // STATE pattern - changer statut avec validation
    public void changerStatut(StatutRendezVous nouveauStatut) {
        if (!this.statut.peutTransitionnerVers(nouveauStatut)) {
            throw new IllegalStateException("Transition invalide : " + this.statut + " → " + nouveauStatut);
        }
        this.statut = nouveauStatut;
        this.dateMaj = LocalDateTime.now();
    }
}