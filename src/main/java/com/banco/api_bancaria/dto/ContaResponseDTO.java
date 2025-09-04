package com.banco.api_bancaria.dto;

import com.banco.api_bancaria.enums.MovimentoTransacao;
import com.banco.api_bancaria.model.ContaBancaria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ContaResponseDTO {
    private String numero;
    private BigDecimal saldo;
    private String tipo;

    public ContaResponseDTO(ContaBancaria contaOrigem) {
    }

}
