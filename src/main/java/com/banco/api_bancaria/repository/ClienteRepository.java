package com.banco.api_bancaria.repository;

import com.banco.api_bancaria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    boolean existsByCpf(String cpf);
}
