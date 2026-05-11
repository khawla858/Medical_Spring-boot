package com.clinique.clinique.controller;

import com.clinique.clinique.model.*;
import com.clinique.clinique.repository.*;
import com.clinique.clinique.service.CliniqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medecins")
@CrossOrigin(origins = "*")
public class MedecinController {
    
    private final CliniqueService cliniqueService;
    private final RendezVousRepository rendezVousRepository;
    private final PlanningRepository planningRepository;
    private final MedecinRepository medecinRepository;
    private final ConsultationRepository consultationRepository;
    
    public MedecinController(CliniqueService cliniqueService,
                             RendezVousRepository rendezVousRepository,
                             PlanningRepository planningRepository,
                             MedecinRepository medecinRepository,
                             ConsultationRepository consultationRepository) {
        this.cliniqueService = cliniqueService;
        this.rendezVousRepository = rendezVousRepository;
        this.planningRepository = planningRepository;
        this.medecinRepository = medecinRepository;
        this.consultationRepository = consultationRepository;
    }
    
    // ✅ ENDPOINT PRINCIPAL - Version sans boucle infinie
    @GetMapping
    public ResponseEntity<?> getAllMedecins() {
        try {
            List<Medecin> medecins = medecinRepository.findAll();
            
            // Construction manuelle du JSON - ÉVITE les références circulaires
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Medecin m : medecins) {
                Map<String, Object> medMap = new LinkedHashMap<>();
                medMap.put("id", m.getId());
                medMap.put("specialite", m.getSpecialite());
                medMap.put("tarifBase", m.getTarifBase());
                medMap.put("bio", m.getBio());
                medMap.put("photoUrl", m.getPhotoUrl());
                
                // Extraire UNIQUEMENT les infos utilisateur (sans référence retour)
                if (m.getUtilisateur() != null) {
                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("id", m.getUtilisateur().getId());
                    userMap.put("nom", m.getUtilisateur().getNom());
                    userMap.put("prenom", m.getUtilisateur().getPrenom());
                    userMap.put("email", m.getUtilisateur().getEmail());
                    userMap.put("telephone", m.getUtilisateur().getTelephone());
                    userMap.put("role", m.getUtilisateur().getRole().name());
                    // ⚠️ NE PAS inclure "medecin" ici !
                    medMap.put("utilisateur", userMap);
                }
                
                result.add(medMap);
            }
            
            System.out.println("✅ " + result.size() + " médecins retournés (sans boucle infinie)");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors du chargement des médecins",
                "message", e.getMessage()
            ));
        }
    }
    
    // Version alternative avec DTO (plus propre)
    @GetMapping("/v2")
    public ResponseEntity<?> getAllMedecinsV2() {
        List<Medecin> medecins = medecinRepository.findAll();
        List<Map<String, Object>> result = medecins.stream()
            .map(this::toSimpleMap)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> toSimpleMap(Medecin m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("specialite", m.getSpecialite());
        map.put("tarifBase", m.getTarifBase());
        if (m.getUtilisateur() != null) {
            map.put("nom", m.getUtilisateur().getNom());
            map.put("prenom", m.getUtilisateur().getPrenom());
            map.put("email", m.getUtilisateur().getEmail());
        }
        return map;
    }
    
    // ✅ Récupérer un médecin par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedecinById(@PathVariable Long id) {
        return medecinRepository.findById(id)
            .map(m -> {
                Map<String, Object> result = toSimpleMap(m);
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ Récupérer par email
    @GetMapping("/by-email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return medecinRepository.findByUtilisateurEmail(email)
            .map(this::toSimpleMap)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ Rendez-vous d'un médecin
    @GetMapping("/{medecinId}/rdv")
    public ResponseEntity<?> getRendezVous(@PathVariable Long medecinId) {
        List<RendezVous> rdvs = rendezVousRepository.findByMedecinIdOrderByDateRdvAsc(medecinId);
        List<Map<String, Object>> result = rdvs.stream()
            .map(rdv -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", rdv.getId());
                map.put("dateRdv", rdv.getDateRdv());
                map.put("motif", rdv.getMotif());
                map.put("statut", rdv.getStatut().name());
                map.put("typeFacturation", rdv.getTypeFacturation().name());
                map.put("montantFinal", rdv.getMontantFinal());
                
                if (rdv.getPatient() != null && rdv.getPatient().getUtilisateur() != null) {
                    map.put("patientNom", rdv.getPatient().getUtilisateur().getNomComplet());
                    map.put("patientId", rdv.getPatient().getId());
                }
                return map;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    // ✅ Rendez-vous confirmés
    @GetMapping("/{medecinId}/rdv/confirmed")
    public ResponseEntity<?> getRendezVousConfirmed(@PathVariable Long medecinId) {
        List<RendezVous> rdvs = rendezVousRepository
            .findByMedecinIdAndStatut(medecinId, StatutRendezVous.CONFIRMED);
        List<Map<String, Object>> result = rdvs.stream()
            .map(rdv -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", rdv.getId());
                map.put("dateRdv", rdv.getDateRdv());
                map.put("patientNom", rdv.getPatient().getUtilisateur().getNomComplet());
                map.put("patientId", rdv.getPatient().getId());
                return map;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    // ✅ Planning d'un médecin
    @GetMapping("/{medecinId}/planning")
    public ResponseEntity<?> getPlanning(@PathVariable Long medecinId) {
        List<Planning> plannings = planningRepository.findByMedecinId(medecinId);
        List<Map<String, Object>> result = plannings.stream()
            .map(p -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", p.getId());
                map.put("jourSemaine", p.getJourSemaine());
                map.put("heureDebut", p.getHeureDebut().toString());
                map.put("heureFin", p.getHeureFin().toString());
                map.put("actif", p.getActif());
                return map;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    // ✅ Ajouter un planning
    @PostMapping("/{medecinId}/planning")
    public ResponseEntity<?> ajouterPlanning(
            @PathVariable Long medecinId,
            @RequestBody Map<String, String> body) {

        String jourSemaine = body.get("jourSemaine");
        String heureDebut = body.get("heureDebut");
        String heureFin = body.get("heureFin");
        
        Medecin medecin = medecinRepository.findById(medecinId)
            .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        
        Planning planning = new Planning();
        planning.setMedecin(medecin);
        planning.setJourSemaine(jourSemaine);
        planning.setHeureDebut(LocalTime.parse(heureDebut));
        planning.setHeureFin(LocalTime.parse(heureFin));
        planning.setActif(true);
        
        planningRepository.save(planning);
        return ResponseEntity.ok(Map.of("message", "Planning ajouté"));
    }
    
    // ✅ Confirmer un rendez-vous
    @PutMapping("/rdv/{rdvId}/confirmer")
    public ResponseEntity<?> confirmerRendezVous(@PathVariable Long rdvId) {
        cliniqueService.confirmerRendezVous(rdvId);
        return ResponseEntity.ok(Map.of("message", "Rendez-vous confirmé"));
    }
    
    // ✅ Refuser un rendez-vous
    @PutMapping("/rdv/{rdvId}/refuser")
    public ResponseEntity<?> refuserRendezVous(@PathVariable Long rdvId) {
        cliniqueService.annulerRendezVous(rdvId);
        return ResponseEntity.ok(Map.of("message", "Rendez-vous refusé"));
    }
    
    // ✅ Ajouter une consultation
    @PostMapping("/consultation/{rdvId}")
    public ResponseEntity<?> addConsultation(@PathVariable Long rdvId,
                                             @RequestBody Map<String, String> body) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        Consultation consultation = new Consultation();
        consultation.setRendezVous(rdv);
        consultation.setDiagnostic(body.get("diagnostic"));
        consultation.setPrescription(body.get("prescription"));
        consultation.setObservations(body.get("observations"));
        consultation.setDateConsultation(LocalDateTime.now());
        
        consultationRepository.save(consultation);
        
        rdv.setStatut(StatutRendezVous.COMPLETED);
        rendezVousRepository.save(rdv);
        
        return ResponseEntity.ok(Map.of("message", "Consultation enregistrée"));
    }
    
    // ✅ Patients d'un médecin
    @GetMapping("/{medecinId}/patients")
    public ResponseEntity<?> getPatientsByMedecin(@PathVariable Long medecinId) {
        List<RendezVous> rdvs = rendezVousRepository.findByMedecinId(medecinId);
        Set<Map<String, Object>> patients = new LinkedHashSet<>();
        
        for (RendezVous rdv : rdvs) {
            if (rdv.getPatient() != null && rdv.getPatient().getUtilisateur() != null) {
                Map<String, Object> patientMap = new LinkedHashMap<>();
                patientMap.put("id", rdv.getPatient().getId());
                patientMap.put("nom", rdv.getPatient().getUtilisateur().getNom());
                patientMap.put("prenom", rdv.getPatient().getUtilisateur().getPrenom());
                patientMap.put("email", rdv.getPatient().getUtilisateur().getEmail());
                patients.add(patientMap);
            }
        }
        
        return ResponseEntity.ok(patients);
    }
}