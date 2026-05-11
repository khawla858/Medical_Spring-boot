package com.clinique.clinique.repository;


import com.clinique.clinique.model.Medecin;
import com.clinique.clinique.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUtilisateurId(Long utilisateurId);
    Optional<Patient> findByNumeroDossier(String numeroDossier);
    boolean existsByCin(String cin);
    Optional<Patient> findByUtilisateurEmail(String email);
    

}