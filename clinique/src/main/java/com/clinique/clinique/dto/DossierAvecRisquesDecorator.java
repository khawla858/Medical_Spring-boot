package com.clinique.clinique.dto;


public class DossierAvecRisquesDecorator extends DossierDecorator {
    
    public DossierAvecRisquesDecorator(DossierDto dossier) {
        super(dossier);
    }
    
    @Override
    public String getInfosCompletes() {
        String risques = evaluerRisques();
        return dossier.getNomComplet() + " - " + risques;
    }
    
    private String evaluerRisques() {
        if (dossier.getAllergies() != null && !dossier.getAllergies().isEmpty()) {
            return "⚠️ Risque allergique: " + dossier.getAllergies();
        }
        if (dossier.getAntecedents() != null && dossier.getAntecedents().toLowerCase().contains("diabète")) {
            return "⚠️ Patient diabétique - surveillance requise";
        }
        return "✅ Aucun risque majeur identifié";
    }
}