package com.clinique.clinique.service;

import com.clinique.clinique.model.*;
import com.clinique.clinique.repository.*;
import com.clinique.clinique.service.factory.UtilisateurFactory;
import com.clinique.clinique.service.observer.NotificationPublisher;
import com.clinique.clinique.service.strategy.FacturationContext;
import com.clinique.clinique.service.strategy.StrategieFacturation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CliniqueService {  // PATTERN FACADE - orchestration complète
    
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationPublisher notificationPublisher;
    private final FacturationContext facturationContext;
    
    // Constructeur explicite (sans @RequiredArgsConstructor)
    public CliniqueService(UtilisateurRepository utilisateurRepository,
                           PatientRepository patientRepository,
                           MedecinRepository medecinRepository,
                           RendezVousRepository rendezVousRepository,
                           ConsultationRepository consultationRepository,
                           NotificationPublisher notificationPublisher,
                           FacturationContext facturationContext) {
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.consultationRepository = consultationRepository;
        this.notificationPublisher = notificationPublisher;
        this.facturationContext = facturationContext;
    }
    
    // FACADE: Inscription utilisateur
    @Transactional
    public Utilisateur inscrireUtilisateur(String nom, String prenom, String email, 
                                           String password, String telephone, Role role) {
        // 1. Créer l'utilisateur
        Utilisateur utilisateur = UtilisateurFactory.creerUtilisateur(nom, prenom, email, password, role);
        utilisateur.setTelephone(telephone);
        utilisateur = utilisateurRepository.save(utilisateur);
        
        // 2. Créer le profil spécifique selon le rôle
        if (role == Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUtilisateur(utilisateur);
            patient.setNumeroDossier("DOS-" + utilisateur.getId() + "-" + System.currentTimeMillis());
            patient.setDateCreation(LocalDateTime.now());
            // Valeurs par défaut (seront complétées plus tard par le patient)
            patient.setAllergies("Non renseigné");
            patient.setAntecedents("Non renseigné");
            
            patientRepository.save(patient);
            System.out.println("✅ Patient créé automatiquement pour: " + email);
        } 
        else if (role == Role.DOCTOR) {
            Medecin medecin = new Medecin();
            medecin.setUtilisateur(utilisateur);
            medecin.setSpecialite("Médecine Générale");
            medecin.setNumeroOrdre("ORD-" + System.currentTimeMillis());
            medecin.setTarifBase(150.0);
            medecin.setDateCreation(LocalDateTime.now());
            medecinRepository.save(medecin);
        }
        
        return utilisateur;
    }
    
    // FACADE: Prendre rendez-vous
    @Transactional
    public RendezVous prendreRendezVous(Long patientId, Long medecinId, 
                                        LocalDateTime date, String motif, 
                                        TypeFacturation typeFacturation) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Medecin medecin = medecinRepository.findById(medecinId)
            .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        
        // Vérifier disponibilité
        if (!estDisponible(medecinId, date)) {
            throw new RuntimeException("Médecin non disponible à cette date");
        }
        
        // Calculer montant (STRATEGY pattern)
        StrategieFacturation strategy = facturationContext.getStrategy(typeFacturation);
        double montant = strategy.calculerMontant(medecin.getTarifBase());
        
        // Créer rendez-vous (sans Builder)
        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setDateRdv(date);
        rdv.setMotif(motif);
        rdv.setTypeFacturation(typeFacturation);
        rdv.setMontantFinal(montant);
        rdv.setStatut(StatutRendezVous.PENDING);
        rdv.setDateCreation(LocalDateTime.now());
        
        rdv = rendezVousRepository.save(rdv);
        
        // Notifier (OBSERVER pattern)
        notificationPublisher.notifier("NOUVEAU_RDV", patient.getUtilisateur(), 
            "Votre rendez-vous avec Dr. " + medecin.getNomComplet() + " a été créé");
        notificationPublisher.notifier("NOUVEAU_RDV", medecin.getUtilisateur(),
            "Nouveau rendez-vous avec " + patient.getUtilisateur().getNomComplet());
        
        return rdv;
    }
    
    // FACADE: Annuler rendez-vous
    @Transactional
    public void annulerRendezVous(Long rdvId) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        rdv.changerStatut(StatutRendezVous.CANCELED);
        rendezVousRepository.save(rdv);
        
        notificationPublisher.notifier("ANNULATION_RDV", rdv.getPatient().getUtilisateur(),
            "Votre rendez-vous a été annulé");
        notificationPublisher.notifier("ANNULATION_RDV", rdv.getMedecin().getUtilisateur(),
            "Un rendez-vous a été annulé");
    }
    
    // FACADE: Confirmer rendez-vous
    @Transactional
    public void confirmerRendezVous(Long rdvId) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        rdv.changerStatut(StatutRendezVous.CONFIRMED);
        rendezVousRepository.save(rdv);
        
        notificationPublisher.notifier("CONFIRMATION_RDV", rdv.getPatient().getUtilisateur(),
            "Votre rendez-vous a été confirmé pour le " + rdv.getDateRdv());
    }
    
    // FACADE: Obtenir statistiques dashboard
    public Map<String, Object> getDashboardStats() {
        long patients = utilisateurRepository.countByRole(Role.PATIENT);
        long medecins = utilisateurRepository.countByRole(Role.DOCTOR);
        long rdvTotal = rendezVousRepository.count();
        long rdvPending = rendezVousRepository.countByStatut(StatutRendezVous.PENDING);
        long rdvConfirmed = rendezVousRepository.countByStatut(StatutRendezVous.CONFIRMED);
        long rdvCanceled = rendezVousRepository.countByStatut(StatutRendezVous.CANCELED);
        long rdvCompleted = rendezVousRepository.countByStatut(StatutRendezVous.COMPLETED);  // ← AJOUTER

        
        return Map.of(
            "patients", patients,
            "medecins", medecins,
            "rendezVousTotal", rdvTotal,
            "rendezVousPending", rdvPending,
            "rendezVousConfirmes", rdvConfirmed,
            "rendezVousAnnules", rdvCanceled,
            "rendezVousTermines", rdvCompleted  // ← AJOUTER

        );
    }
    
    private boolean estDisponible(Long medecinId, LocalDateTime date) {
        List<RendezVous> rdvs = rendezVousRepository.findByMedecinAndPeriode(
            medecinId, date.withMinute(0).withSecond(0), date.withMinute(59).withSecond(59));
        return rdvs.isEmpty();
    }
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient introuvable avec id: " + id));
    }
    
    public Optional<Patient> findPatientByEmail(String email) {
        return patientRepository.findByUtilisateurEmail(email);
    }
    
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByUtilisateurEmail(email)
            .orElseThrow(() -> new RuntimeException("Patient non trouvé avec email: " + email));
    }
}