package com.clinique.clinique.config;

import org.springframework.context.annotation.Configuration;

@Configuration // Déjà un Singleton Spring
 public class AppConfig {

 public static class ConfigClinique {
 private String nomClinique = "Clinique Al-Amal";
 private int capaciteMax = 500;

 private static class Holder { 
 static final ConfigClinique INSTANCE = new ConfigClinique();
 }
 private ConfigClinique() {} // constructeur privé
 public static ConfigClinique getInstance() { return Holder.INSTANCE; }

 public String getNomClinique() { return nomClinique; }
 public int getCapaciteMax() { return capaciteMax; }
 }
 }
