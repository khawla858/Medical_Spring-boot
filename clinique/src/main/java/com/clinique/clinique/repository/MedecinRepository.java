package com.clinique.clinique.repository;


import com.clinique.clinique.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    List<Medecin> findBySpecialite(String specialite);
    Optional<Medecin> findByUtilisateurId(Long utilisateurId);
    Optional<Medecin> findByNumeroOrdre(String numeroOrdre);
    @Query("SELECT DISTINCT m.specialite FROM Medecin m ORDER BY m.specialite")
    List<String> findAllSpecialites();
    Optional<Medecin> findByUtilisateurEmail(String email);
}
