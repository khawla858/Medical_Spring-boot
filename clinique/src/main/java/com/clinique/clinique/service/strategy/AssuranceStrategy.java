package com.clinique.clinique.service.strategy;


public class AssuranceStrategy implements StrategieFacturation {
    @Override
    public double calculerMontant(double tarifBase) {
        // Patient paye 20%, assurance prend 80%
        return tarifBase * 0.2;
    }
    
    @Override
    public String getDescription() {
        return "Assurance - Patient paye 20%, assurance 80%";
    }
}