package com.clinique.clinique.dto;


public abstract class DossierDecorator extends DossierDto {
    protected DossierDto dossier;
    
    public DossierDecorator(DossierDto dossier) {
        super(null);
        this.dossier = dossier;
    }
    
    public abstract String getInfosCompletes();
}