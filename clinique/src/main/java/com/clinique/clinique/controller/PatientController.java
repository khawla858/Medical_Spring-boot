package com.clinique.clinique.controller;

import com.clinique.clinique.dto.RendezVousDto;
import com.clinique.clinique.model.Consultation;
import com.clinique.clinique.model.Patient;
import com.clinique.clinique.model.RendezVous;
import com.clinique.clinique.model.TypeFacturation;
import com.clinique.clinique.model.Utilisateur;
import com.clinique.clinique.repository.PatientRepository;
import com.clinique.clinique.repository.RendezVousRepository;
import com.clinique.clinique.repository.UtilisateurRepository;
import com.clinique.clinique.service.CliniqueService;
import com.clinique.clinique.service.HistoriqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    private final CliniqueService cliniqueService;
    private final HistoriqueService historiqueService;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;  // ← AJOUTER

    public PatientController(CliniqueService cliniqueService, 
                             HistoriqueService historiqueService,
                             PatientRepository patientRepository,
                             UtilisateurRepository utilisateurRepository,
                             RendezVousRepository rendezVousRepository) {
        this.cliniqueService = cliniqueService;
        this.historiqueService = historiqueService;
        this.patientRepository = patientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.rendezVousRepository = rendezVousRepository;
    }
    
    // ==================== PROFIL ====================
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientProfile(@PathVariable Long id) {
        try {
            Patient patient = cliniqueService.getPatientById(id);
            if (patient != null) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("id", patient.getId());
                result.put("numeroDossier", patient.getNumeroDossier());
                result.put("cin", patient.getCin());
                result.put("dateNaissance", patient.getDateNaissance());
                result.put("groupeSanguin", patient.getGroupeSanguin());
                result.put("adresse", patient.getAdresse());
                result.put("allergies", patient.getAllergies());
                result.put("antecedents", patient.getAntecedents());
                
                if (patient.getUtilisateur() != null) {
                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("id", patient.getUtilisateur().getId());
                    userMap.put("nom", patient.getUtilisateur().getNom());
                    userMap.put("prenom", patient.getUtilisateur().getPrenom());
                    userMap.put("email", patient.getUtilisateur().getEmail());
                    userMap.put("telephone", patient.getUtilisateur().getTelephone());
                    result.put("utilisateur", userMap);
                }
                
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/by-email")
    public ResponseEntity<?> getPatientByEmail(@RequestParam String email) {
        try {
            Patient patient = cliniqueService.getPatientByEmail(email);
            if (patient != null) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("id", patient.getId());
                result.put("numeroDossier", patient.getNumeroDossier());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== RENDEZ-VOUS ====================
    
    // ✅ LISTER les rendez-vous du patient
    @GetMapping("/{patientId}/rdv")
    public ResponseEntity<?> getPatientRendezVous(@PathVariable Long patientId) {
        try {
            System.out.println("=== getPatientRendezVous - patientId: " + patientId);
            
            List<RendezVous> rdvs = rendezVousRepository.findByPatientIdOrderByDateRdvDesc(patientId);
            
            System.out.println("Nombre de RDV trouvés: " + rdvs.size());
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (RendezVous rdv : rdvs) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", rdv.getId());
                map.put("dateRdv", rdv.getDateRdv());
                map.put("motif", rdv.getMotif());
                map.put("statut", rdv.getStatut().name());
                map.put("typeFacturation", rdv.getTypeFacturation().name());
                map.put("montantFinal", rdv.getMontantFinal());
                
                if (rdv.getMedecin() != null && rdv.getMedecin().getUtilisateur() != null) {
                    map.put("medecinNom", rdv.getMedecin().getUtilisateur().getPrenom() + " " + rdv.getMedecin().getUtilisateur().getNom());
                }
                result.add(map);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("ERREUR getPatientRendezVous: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ CREER un rendez-vous
    @PostMapping("/{patientId}/rdv")
    public ResponseEntity<?> prendreRendezVous(
            @PathVariable Long patientId,
            @RequestParam Long medecinId,
            @RequestParam String dateStr,
            @RequestParam String motif,
            @RequestParam String typeFacturation) {
        
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr);
            TypeFacturation type = TypeFacturation.valueOf(typeFacturation.toUpperCase());
            
            RendezVous rdv = cliniqueService.prendreRendezVous(patientId, medecinId, date, motif, type);
            
            return ResponseEntity.ok(new RendezVousDto(rdv));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ ANNULER un rendez-vous
    @DeleteMapping("/rdv/{rdvId}")
    public ResponseEntity<?> annulerRendezVous(@PathVariable Long rdvId) {
        try {
            cliniqueService.annulerRendezVous(rdvId);
            return ResponseEntity.ok(Map.of("message", "Rendez-vous annulé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== HISTORIQUE ====================
    
    @GetMapping("/{patientId}/historique")
    public ResponseEntity<?> getHistorique(@PathVariable Long patientId) {
        try {
            var iterator = historiqueService.getIterateurAvecPagination(patientId, 0, 10);
            
            List<Map<String, Object>> historique = new ArrayList<>();
            while (iterator.hasNext()) {
                Consultation consultation = iterator.next();
                RendezVous rdv = consultation.getRendezVous();
                
                Map<String, Object> consultMap = new LinkedHashMap<>();
                consultMap.put("id", consultation.getId());
                consultMap.put("dateRdv", rdv.getDateRdv());
                consultMap.put("motif", rdv.getMotif());
                consultMap.put("diagnostic", consultation.getDiagnostic());
                consultMap.put("prescription", consultation.getPrescription());
                consultMap.put("observations", consultation.getObservations());
                
                if (rdv.getMedecin() != null && rdv.getMedecin().getUtilisateur() != null) {
                    consultMap.put("medecinNom", rdv.getMedecin().getUtilisateur().getPrenom() + " " + rdv.getMedecin().getUtilisateur().getNom());
                    consultMap.put("medecinSpec", rdv.getMedecin().getSpecialite());
                }
                
                historique.add(consultMap);
            }
            
            return ResponseEntity.ok(Map.of(
                "historique", historique,
                "totalPages", iterator.getTotalPages(),
                "totalElements", iterator.getTotalElements()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== DOSSIER MÉDICAL ====================
    
    @PutMapping("/{id}/dossier-medical")
    public ResponseEntity<?> updateDossierMedical(
            @PathVariable Long id,
            @RequestBody Map<String, Object> dossier) {
        try {
            Patient patient = cliniqueService.getPatientById(id);
            
            if (dossier.containsKey("cin")) patient.setCin((String) dossier.get("cin"));
            if (dossier.containsKey("dateNaissance") && dossier.get("dateNaissance") != null && !((String)dossier.get("dateNaissance")).isEmpty()) {
                patient.setDateNaissance(LocalDate.parse((String) dossier.get("dateNaissance")));
            }
            if (dossier.containsKey("groupeSanguin")) patient.setGroupeSanguin((String) dossier.get("groupeSanguin"));
            if (dossier.containsKey("adresse")) patient.setAdresse((String) dossier.get("adresse"));
            if (dossier.containsKey("allergies")) patient.setAllergies((String) dossier.get("allergies"));
            if (dossier.containsKey("antecedents")) patient.setAntecedents((String) dossier.get("antecedents"));
            
            patientRepository.save(patient);
            
            return ResponseEntity.ok(Map.of("message", "Dossier médical mis à jour"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/create-from-user")
    public ResponseEntity<?> createPatientFromUser(@RequestParam String email) {
        try {
            Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            Optional<Patient> existing = patientRepository.findByUtilisateurId(user.getId());
            if (existing.isPresent()) {
                return ResponseEntity.ok(Map.of("message", "Patient existe déjà", "patientId", existing.get().getId()));
            }
            
            Patient patient = new Patient();
            patient.setUtilisateur(user);
            patient.setNumeroDossier("DOS-" + user.getId() + "-" + System.currentTimeMillis());
            patient.setDateCreation(LocalDateTime.now());
            
            patientRepository.save(patient);
            
            return ResponseEntity.ok(Map.of(
                "message", "Patient créé avec succès",
                "patientId", patient.getId(),
                "numeroDossier", patient.getNumeroDossier()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}