package com.banco.api_bancaria.dto;

import com.banco.api_bancaria.enums.TipoTransacao;
import com.banco.api_bancaria.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExtratoDTO(
        LocalDateTime data,
        BigDecimal valor,
        TipoTransacao tipo,
        String contaOrigem,
        String contaDestino
) {
    public static ExtratoDTO fromEntity(Transacao transacao){
        return new ExtratoDTO(
                transacao.getDataHora(),
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getContaOrigem().getNumero(),
                transacao.getContaDestino().getNumero()
        );
    }
}

