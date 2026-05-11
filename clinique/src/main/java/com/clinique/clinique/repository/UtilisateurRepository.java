package com.clinique.clinique.repository;


import com.clinique.clinique.model.Role;
import com.clinique.clinique.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRole(Role role);
    List<Utilisateur> findByRoleAndActif(Role role, Boolean actif);
    long countByRole(Role role);
    
}