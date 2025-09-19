package com.banco.api_bancaria.repository;

import com.banco.api_bancaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    Optional<Usuario> findByUsername(String username);
}
