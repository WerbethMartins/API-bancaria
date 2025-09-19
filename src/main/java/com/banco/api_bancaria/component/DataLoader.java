package com.banco.api_bancaria.component;

import com.banco.api_bancaria.model.Usuario;
import com.banco.api_bancaria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Criando usuário admin inicial
        if(usuarioRepository.findByUsername("admin").isEmpty()){
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(List.of("ADMIN"));
            System.out.println("Usuário admin criado com sucesso!");
            usuarioRepository.save(admin);
        }
    }
}
