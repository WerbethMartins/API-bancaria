package com.banco.api_bancaria.controller;

import com.banco.api_bancaria.component.JwtUtil;
import com.banco.api_bancaria.dto.userValidation.JwtResponse;
import com.banco.api_bancaria.dto.userValidation.LoginRequestDTO;
import com.banco.api_bancaria.dto.userValidation.RegisterDTO;
import com.banco.api_bancaria.model.Usuario;
import com.banco.api_bancaria.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        try{
            JwtResponse response = authService.loginUser(loginRequestDTO);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credencias inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO){
        try {
            Usuario usuario = authService.registerUser(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "Message", "Usuário criado com sucesso!",
                    "username", usuario.getUsername()
            ));
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("Error", e.getMessage()));
        }
    }

}
