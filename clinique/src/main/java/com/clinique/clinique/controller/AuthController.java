package com.clinique.clinique.controller;

import com.clinique.clinique.model.Role;
import com.clinique.clinique.model.Utilisateur;
import com.clinique.clinique.repository.UtilisateurRepository;
import com.clinique.clinique.security.JwtTokenProvider;
import com.clinique.clinique.service.CliniqueService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	 private final AuthenticationManager authenticationManager;
	    private final JwtTokenProvider tokenProvider;
	    private final UtilisateurRepository utilisateurRepository;
	    private final PasswordEncoder passwordEncoder;
	    private final CliniqueService cliniqueService;  // ← AJOUTER

	    public AuthController(AuthenticationManager authenticationManager,
	                          JwtTokenProvider tokenProvider,
	                          UtilisateurRepository utilisateurRepository,
	                          PasswordEncoder passwordEncoder,
	                          CliniqueService cliniqueService) {  // ← AJOUTER
	        this.authenticationManager = authenticationManager;
	        this.tokenProvider = tokenProvider;
	        this.utilisateurRepository = utilisateurRepository;
	        this.passwordEncoder = passwordEncoder;
	        this.cliniqueService = cliniqueService;  // ← AJOUTER
	    }
	    @PostMapping("/register")
	    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
	        try {
	            String nom = request.get("nom");
	            String prenom = request.get("prenom");
	            String email = request.get("email");
	            String password = request.get("password");
	            String telephone = request.get("telephone");
	            String roleStr = request.get("role");

	            // Validation
	            if (nom == null || prenom == null || email == null || password == null) {
	                return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs sont requis"));
	            }

	            if (utilisateurRepository.existsByEmail(email)) {
	                return ResponseEntity.badRequest().body(Map.of("error", "Email déjà utilisé"));
	            }

	            Role role = roleStr != null ? Role.valueOf(roleStr.toUpperCase()) : Role.PATIENT;
	            
	            // ✅ UTILISER le service qui crée automatiquement le patient !
	            Utilisateur utilisateur = cliniqueService.inscrireUtilisateur(
	                nom, prenom, email, password, telephone, role
	            );

	            Map<String, Object> response = new HashMap<>();
	            response.put("success", true);
	            response.put("message", "Inscription réussie");
	            response.put("id", utilisateur.getId());
	            response.put("email", utilisateur.getEmail());
	            response.put("role", utilisateur.getRole());
	            
	            return ResponseEntity.ok(response);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Erreur lors de l'inscription: " + e.getMessage()));
	        }
	    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            System.out.println("Tentative de connexion: " + email);
            
            // Vérifier si l'utilisateur existe
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
            
            System.out.println("Utilisateur trouvé: " + utilisateur.getEmail());
            System.out.println("Mot de passe stocké: " + utilisateur.getMotDePasse());
            System.out.println("Mot de passe fourni: " + password);
            System.out.println("PasswordEncoder: " + passwordEncoder.getClass().getName());
            
            // Vérifier le mot de passe manuellement
            boolean matches = passwordEncoder.matches(password, utilisateur.getMotDePasse());
            System.out.println("Mot de passe correspond: " + matches);
            
            if (!matches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
            }
            
            // Authentification Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            String token = tokenProvider.generateToken(authentication);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("email", email);
            response.put("role", utilisateur.getRole().toString());
            response.put("message", "Connexion réussie");
            
            System.out.println("Connexion réussie pour: " + email);
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            System.out.println("BadCredentialsException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Email ou mot de passe incorrect"));
        } catch (UsernameNotFoundException e) {
            System.out.println("UsernameNotFoundException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Email ou mot de passe incorrect"));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}