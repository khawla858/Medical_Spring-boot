package com.clinique.clinique.repository;


import com.clinique.clinique.model.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {
    List<Planning> findByMedecinIdAndActif(Long medecinId, Boolean actif);
    List<Planning> findByMedecinId(Long medecinId);
    void deleteByMedecinId(Long medecinId);
}