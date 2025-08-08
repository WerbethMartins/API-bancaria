package com.banco.api_bancaria.repository;

import com.banco.api_bancaria.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long>{
    Optional<ContaBancaria> findByNumero(String numero);
}
