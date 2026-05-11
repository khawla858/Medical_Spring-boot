package com.clinique.clinique.service.strategy;


public interface StrategieFacturation {  // PATTERN STRATEGY - interface
    double calculerMontant(double tarifBase);
    String getDescription();
}