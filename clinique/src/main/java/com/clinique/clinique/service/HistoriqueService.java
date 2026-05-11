package com.clinique.clinique.service;


import com.clinique.clinique.model.Consultation;
import com.clinique.clinique.model.RendezVous;
import com.clinique.clinique.repository.ConsultationRepository;
import com.clinique.clinique.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HistoriqueService {  // PATTERN ITERATOR
    
    private final RendezVousRepository rendezVousRepository;
    private final ConsultationRepository consultationRepository;
    public HistoriqueService(RendezVousRepository rendezVousRepository,ConsultationRepository consultationRepository) {
    	this.rendezVousRepository=rendezVousRepository;
    	this.consultationRepository=consultationRepository;
    }
    
    // ITERATOR: Parcours personnalisé des consultations
    public Iterator<Consultation> getHistoriqueParPeriode(Long patientId, 
                                                            LocalDateTime debut, 
                                                            LocalDateTime fin) {
        List<RendezVous> rdvs = rendezVousRepository.findByPatientId(patientId);
        List<Consultation> consultations = new ArrayList<>();
        
        for (RendezVous rdv : rdvs) {
            if (rdv.getConsultation() != null) {
                LocalDateTime date = rdv.getDateRdv();
                if ((debut == null || date.isAfter(debut)) && 
                    (fin == null || date.isBefore(fin))) {
                    consultations.add(rdv.getConsultation());
                }
            }
        }
        
        // Tri par date décroissante
        consultations.sort((c1, c2) -> c2.getDateConsultation().compareTo(c1.getDateConsultation()));
        
        return consultations.iterator();
    }
    
    // ITERATOR personnalisé avec pagination
    public IterateurConsultations getIterateurAvecPagination(Long patientId, int page, int size) {
        List<RendezVous> rdvs = rendezVousRepository.findByPatientIdOrderByDateRdvDesc(patientId);
        List<Consultation> consultations = new ArrayList<>();
        
        for (RendezVous rdv : rdvs) {
            if (rdv.getConsultation() != null) {
                consultations.add(rdv.getConsultation());
            }
        }
        
        return new IterateurConsultations(consultations, page, size);
    }
    
    // Classe interne Iterator personnalisée
    public static class IterateurConsultations implements Iterator<Consultation> {
        private final List<Consultation> consultations;
        private final int page;
        private final int size;
        private int currentIndex = 0;
        
        public IterateurConsultations(List<Consultation> consultations, int page, int size) {
            this.consultations = consultations;
            this.page = page;
            this.size = size;
            this.currentIndex = page * size;
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < consultations.size() && currentIndex < (page + 1) * size;
        }
        
        @Override
        public Consultation next() {
            if (!hasNext()) throw new NoSuchElementException();
            return consultations.get(currentIndex++);
        }
        
        public int getTotalPages() {
            return (int) Math.ceil((double) consultations.size() / size);
        }
        
        public long getTotalElements() {
            return consultations.size();
        }
    }
}