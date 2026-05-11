package com.clinique.clinique.service;

import com.clinique.clinique.model.*;
import com.clinique.clinique.repository.*;
import com.clinique.clinique.service.observer.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ConsultationService {  // PATTERN TEMPLATE METHOD
    
	private final RendezVousRepository rendezVousRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationPublisher notificationPublisher;
    
    public ConsultationService(RendezVousRepository rendezVousRepository,ConsultationRepository consultationRepository, NotificationPublisher notificationPublisher) {
        this.rendezVousRepository = rendezVousRepository;
        this.consultationRepository = consultationRepository;
        this.notificationPublisher = notificationPublisher;
    }
    
    // TEMPLATE METHOD - squelette fixe
    @Transactional
    public final Consultation executerConsultation(Long rdvId, String diagnostic, 
                                                     String prescription, String observations) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        // Étape 1: Valider le rendez-vous
        validerRendezVous(rdv);
        
        // Étape 2: Examiner le patient (étape personnalisable)
        examinerPatient(rdv);
        
        // Étape 3: Prescrire traitement (étape personnalisable)
        prescrireTraitement(rdv, prescription);
        
        // Étape 4: Clôturer la consultation
        Consultation consultation = cloturerConsultation(rdv, diagnostic, prescription, observations);
        
        // Étape 5: Notifier le patient
        notifierPatient(rdv);
        
        return consultation;
    }
    
    private void validerRendezVous(RendezVous rdv) {
        if (rdv.getStatut() != StatutRendezVous.CONFIRMED) {
            throw new RuntimeException("Le rendez-vous doit être confirmé avant consultation");
        }
        System.out.println("✓ Rendez-vous validé pour: " + rdv.getPatient().getUtilisateur().getNomComplet());
    }
    
    protected void examinerPatient(RendezVous rdv) {
        // Template hook - peut être surchargé par sous-classe
        System.out.println("✓ Examen du patient " + rdv.getPatient().getUtilisateur().getNomComplet());
    }
    
    protected void prescrireTraitement(RendezVous rdv, String prescription) {
        // Template hook - peut être surchargé par sous-classe
        System.out.println("✓ Prescription enregistrée");

    }
    
    private Consultation cloturerConsultation(RendezVous rdv, String diagnostic, 
                                                String prescription, String observations) {
        //  Création sans Builder (constructeur standard + setters)
        Consultation consultation = new Consultation();
        consultation.setRendezVous(rdv);
        consultation.setDiagnostic(diagnostic);
        consultation.setPrescription(prescription);
        consultation.setObservations(observations);
        consultation.setDateConsultation(LocalDateTime.now());
        consultation.setDureeMinutes(30);
        
        consultation = consultationRepository.save(consultation);
        
        rdv.setConsultation(consultation);
        rdv.changerStatut(StatutRendezVous.COMPLETED);
        rendezVousRepository.save(rdv);
        
        System.out.println("✓ Consultation clôturée");
        return consultation;
    }
    
    private void notifierPatient(RendezVous rdv) {
        notificationPublisher.notifier("CONSULTATION_TERMINEE", rdv.getPatient().getUtilisateur(),
            "Votre consultation est terminée. Consultez votre dossier médical pour les détails.");
    }
}