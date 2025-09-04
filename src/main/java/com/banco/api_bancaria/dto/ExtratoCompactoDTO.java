package com.banco.api_bancaria.dto;

import com.banco.api_bancaria.enums.MovimentoTransacao;
import com.banco.api_bancaria.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExtratoCompactoDTO (
        LocalDateTime data,
        MovimentoTransacao movimento, // Entrada ou Saída
        BigDecimal valor
){
    public static ExtratoCompactoDTO fromEntity(Transacao transacao, String numeroContaTitular){
        MovimentoTransacao movimento;

        if(transacao.getContaOrigem() != null && transacao.getContaOrigem().getNumero().equals(numeroContaTitular)){
            movimento = MovimentoTransacao.SAIDA;
        } else if(transacao.getContaDestino() != null && transacao.getContaDestino().getNumero().equals(numeroContaTitular)){
            movimento = MovimentoTransacao.ENTRADA;
        } else {
            throw new IllegalStateException("Transação não relacionada à conta informada!");
        }

        return new ExtratoCompactoDTO(transacao.getDataHora(), movimento, transacao.getValor());
    }
}
