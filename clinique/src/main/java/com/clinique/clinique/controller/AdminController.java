package com.clinique.clinique.controller;

import com.clinique.clinique.model.*;
import com.clinique.clinique.repository.*;
import com.clinique.clinique.service.CliniqueService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final CliniqueService cliniqueService;
    private final RendezVousRepository rendezVousRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(CliniqueService cliniqueService,
                           RendezVousRepository rendezVousRepository,
                           UtilisateurRepository utilisateurRepository,
                           MedecinRepository medecinRepository,
                           PatientRepository patientRepository,
                           PasswordEncoder passwordEncoder) {
        this.cliniqueService = cliniqueService;
        this.rendezVousRepository = rendezVousRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(cliniqueService.getDashboardStats());
    }

    @GetMapping("/medecins")
    public ResponseEntity<?> getAllMedecins() {
        List<Medecin> medecins = medecinRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Medecin m : medecins) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("specialite", m.getSpecialite());
            map.put("tarifBase", m.getTarifBase());
            map.put("bio", m.getBio());
            
            if (m.getUtilisateur() != null) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("id", m.getUtilisateur().getId());
                userMap.put("nom", m.getUtilisateur().getNom());
                userMap.put("prenom", m.getUtilisateur().getPrenom());
                userMap.put("email", m.getUtilisateur().getEmail());
                map.put("utilisateur", userMap);
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Patient p : patients) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("numeroDossier", p.getNumeroDossier());
            map.put("dateCreation", p.getDateCreation());
            
            if (p.getUtilisateur() != null) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("id", p.getUtilisateur().getId());
                userMap.put("nom", p.getUtilisateur().getNom());
                userMap.put("prenom", p.getUtilisateur().getPrenom());
                userMap.put("email", p.getUtilisateur().getEmail());
                userMap.put("telephone", p.getUtilisateur().getTelephone());
                map.put("utilisateur", userMap);
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rdv/recent")
    public ResponseEntity<?> getRecentRdvs() {
        List<RendezVous> rdvs = rendezVousRepository.findAllByOrderByDateRdvDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (RendezVous r : rdvs.stream().limit(10).collect(Collectors.toList())) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("dateRdv", r.getDateRdv());
            map.put("statut", r.getStatut().name());
            map.put("montant", r.getMontantFinal());
            
            if (r.getPatient() != null && r.getPatient().getUtilisateur() != null) {
                map.put("patientNom", r.getPatient().getUtilisateur().getPrenom() + " " + r.getPatient().getUtilisateur().getNom());
            }
            if (r.getMedecin() != null && r.getMedecin().getUtilisateur() != null) {
                map.put("medecinNom", r.getMedecin().getUtilisateur().getPrenom() + " " + r.getMedecin().getUtilisateur().getNom());
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rdv/all")
    public ResponseEntity<?> getAllRdvs() {
        List<RendezVous> rdvs = rendezVousRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (RendezVous r : rdvs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("dateRdv", r.getDateRdv());
            map.put("motif", r.getMotif());
            map.put("statut", r.getStatut().name());
            map.put("montant", r.getMontantFinal());
            
            if (r.getPatient() != null && r.getPatient().getUtilisateur() != null) {
                map.put("patientNom", r.getPatient().getUtilisateur().getPrenom() + " " + r.getPatient().getUtilisateur().getNom());
            }
            if (r.getMedecin() != null && r.getMedecin().getUtilisateur() != null) {
                map.put("medecinNom", r.getMedecin().getUtilisateur().getPrenom() + " " + r.getMedecin().getUtilisateur().getNom());
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/medecins")
    public ResponseEntity<?> addMedecin(@RequestBody Map<String, Object> body) {
        try {
            Utilisateur user = new Utilisateur();
            user.setNom((String) body.get("nom"));
            user.setPrenom((String) body.get("prenom"));
            user.setEmail((String) body.get("email"));
            user.setMotDePasse(passwordEncoder.encode((String) body.get("password")));
            user.setRole(Role.DOCTOR);
            user.setActif(true);
            utilisateurRepository.save(user);
            
            Medecin medecin = new Medecin();
            medecin.setUtilisateur(user);
            medecin.setSpecialite((String) body.get("specialite"));
            medecin.setTarifBase(Double.parseDouble(body.get("tarifBase").toString()));
            medecin.setNumeroOrdre("ORD-" + System.currentTimeMillis());
            medecinRepository.save(medecin);
            
            return ResponseEntity.ok(Map.of("message", "Médecin ajouté avec succès", "id", medecin.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/medecins/{id}")
    public ResponseEntity<?> deleteMedecin(@PathVariable Long id) {
        medecinRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Médecin supprimé"));
    }

    @PutMapping("/patients/{id}/desactiver")
    public ResponseEntity<?> desactiverPatient(@PathVariable Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.getUtilisateur().setActif(false);
        utilisateurRepository.save(patient.getUtilisateur());
        return ResponseEntity.ok(Map.of("message", "Patient désactivé"));
    }
}