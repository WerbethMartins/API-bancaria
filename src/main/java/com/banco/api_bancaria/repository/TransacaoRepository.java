package com.banco.api_bancaria.repository;

import com.banco.api_bancaria.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository  extends JpaRepository<Transacao, Long>{

    List<Transacao> findByContaOrigem_NumeroOrContaDestino_Numero(String numeroOrigem, String numeroDestino);
}
