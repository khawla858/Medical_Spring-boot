package com.clinique.clinique.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN N°10 : PROTOTYPE (Créationnel)      ║
 * ╠══════════════════════════════════════════════════════╣
 * ║  Problème  : Créer un nouveau dossier médical pour  ║
 * ║  un patient de même profil (diabétique, cardiaque)  ║
 * ║  en repartant d'un dossier modèle existant, sans    ║
 * ║  ressaisir toutes les informations de base.         ║
 * ║                                                      ║
 * ║  Solution  : DossierMedical implémente Cloneable.   ║
 * ║  La méthode clone() fait une COPIE PROFONDE         ║
 * ║  (deep copy) de toutes les listes internes pour     ║
 * ║  éviter le partage de références.                   ║
 * ║                                                      ║
 * ║  Utilisation dans le projet :                       ║
 * ║  - Nouveau patient diabétique → cloner le template  ║
 * ║  - Dossier annuel → cloner l'ancien et mettre à    ║
 * ║    jour uniquement l'année et les traitements.      ║
 * ╚══════════════════════════════════════════════════════╝
 */
public class DossierMedical implements Cloneable {

    // ── Identification ────────────────────────────────────────────────
    private Long patientId;
    private String nomPatient;
    private String numeroDossier;
    private int annee;

    // ── Données médicales ─────────────────────────────────────────────
    private String groupeSanguin;
    private List<String> allergies;
    private List<String> antecedentsFamiliaux;
    private List<String> antecedentsPersonnels;
    private List<String> traitementsChroniquesPrescrit;
    private String observations;

    // ── Méta ──────────────────────────────────────────────────────────
    private LocalDateTime dateCreation;
    private LocalDate dateDerniereMAJ;

    // ── Constructeur ──────────────────────────────────────────────────
    public DossierMedical() {
        this.allergies                     = new ArrayList<>();
        this.antecedentsFamiliaux          = new ArrayList<>();
        this.antecedentsPersonnels         = new ArrayList<>();
        this.traitementsChroniquesPrescrit = new ArrayList<>();
        this.dateCreation                  = LocalDateTime.now();
        this.annee                         = LocalDateTime.now().getYear();
    }

    public DossierMedical(Long patientId, String nomPatient, String groupeSanguin) {
        this();
        this.patientId     = patientId;
        this.nomPatient    = nomPatient;
        this.groupeSanguin = groupeSanguin;
        this.numeroDossier = "DOS-" + patientId + "-" + annee;
    }

    // ── CŒUR DU PATTERN PROTOTYPE ─────────────────────────────────────
    /**
     * Clone PROFOND — chaque liste est copiée indépendamment.
     * Modifier la copie NE modifie PAS l'original.
     */
    @Override
    public DossierMedical clone() {
        try {
            DossierMedical copie = (DossierMedical) super.clone();

            // Copie profonde de toutes les listes
            copie.allergies                     = new ArrayList<>(this.allergies);
            copie.antecedentsFamiliaux          = new ArrayList<>(this.antecedentsFamiliaux);
            copie.antecedentsPersonnels         = new ArrayList<>(this.antecedentsPersonnels);
            copie.traitementsChroniquesPrescrit = new ArrayList<>(this.traitementsChroniquesPrescrit);

            // Réinitialiser les méta de la copie
            copie.dateCreation   = LocalDateTime.now();
            copie.dateDerniereMAJ = LocalDate.now();

            System.out.println("[PROTOTYPE] Dossier cloné pour : " + nomPatient);
            return copie;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clonage impossible : " + e.getMessage());
        }
    }

    /**
     * Crée un dossier PERSONNALISÉ pour un nouveau patient
     * à partir de ce dossier modèle (template).
     *
     * Usage :
     *   DossierMedical templateDiabete = new DossierMedical(...);
     *   templateDiabete.ajouterAntecedent("Diabète type 2");
     *   templateDiabete.prescrire("Metformine 500mg");
     *
     *   DossierMedical dossierAli = templateDiabete
     *       .personnaliserPour(42L, "Ali Mansour");
     */
    public DossierMedical personnaliserPour(Long patientId, String nomPatient) {
        DossierMedical copie      = this.clone();
        copie.patientId           = patientId;
        copie.nomPatient          = nomPatient;
        copie.numeroDossier       = "DOS-" + patientId + "-" + copie.annee;
        return copie;
    }

    /**
     * Clone le dossier pour l'année suivante
     * (réutilise les antécédents, remet les traitements à jour).
     */
    public DossierMedical clonerPourNouvelleAnnee() {
        DossierMedical copie = this.clone();
        copie.annee          = this.annee + 1;
        copie.numeroDossier  = "DOS-" + patientId + "-" + copie.annee;
        copie.traitementsChroniquesPrescrit = new ArrayList<>(); // reset
        System.out.println("[PROTOTYPE] Nouveau dossier annuel créé : " + copie.numeroDossier);
        return copie;
    }

    // ── Méthodes métier ───────────────────────────────────────────────
    public void ajouterAllergie(String allergie) {
        allergies.add(allergie);
    }

    public void ajouterAntecedentPersonnel(String antecedent) {
        antecedentsPersonnels.add(antecedent);
    }

    public void ajouterAntecedentFamilial(String antecedent) {
        antecedentsFamiliaux.add(antecedent);
    }

    public void prescrire(String traitement) {
        traitementsChroniquesPrescrit.add(traitement);
    }

    // ── Getters & Setters ─────────────────────────────────────────────
    public Long   getPatientId()    { return patientId; }
    public String getNomPatient()   { return nomPatient; }
    public String getNumeroDossier(){ return numeroDossier; }
    public int    getAnnee()        { return annee; }
    public String getGroupeSanguin(){ return groupeSanguin; }

    public List<String> getAllergies()           { return allergies; }
    public List<String> getAntecedentsFamiliaux(){ return antecedentsFamiliaux; }
    public List<String> getAntecedentsPersonnels(){ return antecedentsPersonnels; }
    public List<String> getTraitementsChroniquesPrescrit() {
        return traitementsChroniquesPrescrit;
    }

    public String getObservations()   { return observations; }
    public void setObservations(String obs) { this.observations = obs; }
    public void setGroupeSanguin(String g)  { this.groupeSanguin = g; }
    public void setPatientId(Long id)       { this.patientId = id; }
    public void setNomPatient(String n)     { this.nomPatient = n; }
    public LocalDateTime getDateCreation()  { return dateCreation; }

    @Override
    public String toString() {
        return "DossierMedical{" +
               "patient='" + nomPatient + '\'' +
               ", dossier='" + numeroDossier + '\'' +
               ", groupe=" + groupeSanguin +
               ", allergies=" + allergies +
               ", annee=" + annee + '}';
    }
}