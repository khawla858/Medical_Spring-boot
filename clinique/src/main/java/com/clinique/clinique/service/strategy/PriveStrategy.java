package com.clinique.clinique.service.strategy;


public class PriveStrategy implements StrategieFacturation {
    @Override
    public double calculerMontant(double tarifBase) {
        // Patient paye 100%
        return tarifBase;
    }
    
    @Override
    public String getDescription() {
        return "Privé - Patient paye la totalité";
    }
}