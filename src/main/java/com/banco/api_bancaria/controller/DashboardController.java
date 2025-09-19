package com.banco.api_bancaria.controller;

import com.banco.api_bancaria.dto.modelDTO.UsuarioDTO;
import com.banco.api_bancaria.model.Usuario;
import com.banco.api_bancaria.repository.ClienteRepository;
import com.banco.api_bancaria.repository.ContaBancariaRepository;
import com.banco.api_bancaria.repository.UsuarioRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(){
        List<UsuarioDTO> usuarios = usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRoles()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("usuarios/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable Long id){
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(new UsuarioDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRoles()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios/{id}/promover")
    public ResponseEntity<Usuario> proverUsuario(@PathVariable Long id){
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setRoles(List.of("ROLE_ADMIN"));
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios/{id}/rebaixar")
    public ResponseEntity<Usuario> rebaixarUsuario(@PathVariable Long id){
        return  usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setRoles(List.of("ROLE_USER"));
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado){
        return usuarioRepository.findById(id)
                .map(usuario -> {
                   usuario.setUsername(usuarioAtualizado.getUsername());
                   usuario.setPassword(usuarioAtualizado.getPassword());
                   return ResponseEntity.ok(usuarioAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> deletarUsuario(@PathVariable Long id){
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
