package com.clinique.clinique.security;

import com.clinique.clinique.repository.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UtilisateurRepository utilisateurRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(UtilisateurRepository utilisateurRepository, JwtTokenProvider jwtTokenProvider) {
        this.utilisateurRepository = utilisateurRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8082",
            "http://127.0.0.1:8082",
            "http://localhost:5500",
            "http://127.0.0.1:5500",
            "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService());
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ✅ CORRIGÉ
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ============================================
                // 1. ENDPOINTS PUBLICS (sans authentification)
                // ============================================
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                
                // ============================================
                // 2. FICHIERS STATIQUES (HTML, CSS, JS)
                // ============================================
                .requestMatchers("/", "/index.html", "/patient.html", "/medecin.html", "/admin.html").permitAll()
                .requestMatchers("/**.html", "/**.css", "/**.js").permitAll()
                
                // ============================================
                // 3. 🔓 TEMPORAIRE : Ouvrir les endpoints pour TEST
                //    À SUPPRIMER EN PRODUCTION !
                // ============================================
                .requestMatchers("/api/medecins").permitAll()  // 🔓 TEMPORAIRE
                .requestMatchers("/api/medecins/**").permitAll()  // 🔓 TEMPORAIRE
                .requestMatchers("/api/patients/**").permitAll()  // 🔓 TEMPORAIRE
                
                // ============================================
                // 4. ENDPOINTS PROTÉGÉS PAR RÔLE
                // ============================================
                // Admin uniquement
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Médecins (DOCTOR et ADMIN)
                .requestMatchers("/api/medecins/by-email").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/api/medecins/{id}/planning/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/api/medecins/{id}/rdv/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/api/medecins/consultation/**").hasRole("DOCTOR")
                
                // Patients
                .requestMatchers("/api/patients/{id}/rdv/**").hasAnyRole("PATIENT", "ADMIN")
                .requestMatchers("/api/patients/rdv/**").hasAnyRole("PATIENT", "ADMIN")
                
                // ============================================
                // 5. TOUTE AUTRE REQUÊTE (par défaut)
                // ============================================
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}