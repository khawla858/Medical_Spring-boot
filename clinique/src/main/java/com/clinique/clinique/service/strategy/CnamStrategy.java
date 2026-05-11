package com.clinique.clinique.service.strategy;


public class CnamStrategy implements StrategieFacturation {
    @Override
    public double calculerMontant(double tarifBase) {
        // Patient paye 30%, CNAM prend 70%
        return tarifBase * 0.3;
    }
    
    @Override
    public String getDescription() {
        return "CNAM - Patient paye 30% du tarif";
    }
}