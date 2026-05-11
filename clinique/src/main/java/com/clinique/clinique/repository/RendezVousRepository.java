package com.clinique.clinique.repository;


import com.clinique.clinique.model.RendezVous;
import com.clinique.clinique.model.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByPatientId(Long patientId);
    List<RendezVous> findByMedecinId(Long medecinId);
    List<RendezVous> findByStatut(StatutRendezVous statut);
    List<RendezVous> findByPatientIdOrderByDateRdvDesc(Long patientId);
    List<RendezVous> findByMedecinIdOrderByDateRdvAsc(Long medecinId);
    
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateRdv BETWEEN :debut AND :fin")
    List<RendezVous> findByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin);
    
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.statut = :statut")
    long countByStatut(StatutRendezVous statut);
    
    long countByMedecinId(Long medecinId);
    long countByPatientId(Long patientId);
    List<RendezVous> findTop5ByOrderByDateRdvDesc();
    List<RendezVous> findByMedecinIdAndStatut(Long medecinId, StatutRendezVous statut);
    List<RendezVous> findAllByOrderByDateRdvDesc();



    
    // ✅ Tous les RDV (pour admin)
    List<RendezVous> findAll();
    
    
}