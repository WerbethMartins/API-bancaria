package com.banco.api_bancaria.repository;

import com.banco.api_bancaria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    Optional<Cliente> findByCpf(String cpf);

    @Query("SELECT c FROM Cliente c JOIN c.contas conta WHERE conta.numero = :numeroConta")
    Cliente findByContaNumero(@Param("numeroConta") String numeroConta);
}
