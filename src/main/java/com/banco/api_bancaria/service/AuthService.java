package com.banco.api_bancaria.service;

import com.banco.api_bancaria.component.JwtUtil;
import com.banco.api_bancaria.dto.userValidation.JwtResponse;
import com.banco.api_bancaria.dto.userValidation.LoginRequestDTO;
import com.banco.api_bancaria.dto.userValidation.RegisterDTO;
import com.banco.api_bancaria.model.Usuario;
import com.banco.api_bancaria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public Usuario registerUser(RegisterDTO registerDTO){
        if(usuarioRepository.existsByUsername(registerDTO.getUsername())){
            throw new RuntimeException("Username já existe!");
        }

        if(usuarioRepository.existsByEmail(registerDTO.getEmail())){
            throw new RuntimeException("Email já existe");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(registerDTO.getUsername());
        novoUsuario.setEmail(registerDTO.getEmail());
        novoUsuario.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        novoUsuario.setRoles(List.of("USER"));

        return usuarioRepository.save(novoUsuario);
    }

    public JwtResponse loginUser(LoginRequestDTO requestDTO){
        try {

            if (requestDTO.getUsername() == null || requestDTO.getUsername().isEmpty()) {
                throw new IllegalArgumentException("Username não pode ser nulo ou vazio!");
            }
            if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Senha não pode ser nula ou vazia!");
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            if (username == null) {
                throw new IllegalArgumentException("Username do UserDetails não pode ser nulo!");
            }

            String token = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            // Gera tokens
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            System.out.println("Username: " + userDetails.getUsername());

            return new JwtResponse(token, refreshToken,username,roles);
        }catch (BadCredentialsException e){
            throw new RuntimeException("Credencias inválidas!");
        } catch (Exception e){
            throw new RuntimeException("Erro ao processar o login: " + e.getMessage());
        }
    }

    public JwtResponse refreshToken(String refreshToken) {
        // Valida token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        // Extrai username do token
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // Carrega usuário
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Gera novos tokens
        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        // Extrai roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(newAccessToken, newRefreshToken, username, roles);
    }
}
