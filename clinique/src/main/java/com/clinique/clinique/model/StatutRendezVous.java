package com.clinique.clinique.model;


public enum StatutRendezVous {
    PENDING("En attente"),
    CONFIRMED("Confirmé"),
    CANCELED("Annulé"),
    COMPLETED("Terminé"),
    RESCHEDULED("Reporté");

    private final String libelle;

    StatutRendezVous(String libelle) { this.libelle = libelle; }

    public String getLibelle() { return libelle; }

    public boolean peutTransitionnerVers(StatutRendezVous cible) {
        return switch (this) {
            case PENDING -> cible == CONFIRMED || cible == CANCELED || cible == RESCHEDULED;
            case CONFIRMED -> cible == COMPLETED || cible == CANCELED || cible == RESCHEDULED;
            case RESCHEDULED -> cible == PENDING || cible == CANCELED;
            case COMPLETED, CANCELED -> false;
        };
    }
}