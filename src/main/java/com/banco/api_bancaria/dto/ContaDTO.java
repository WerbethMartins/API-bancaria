package com.banco.api_bancaria.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContaDTO {
    private String numero;
    private BigDecimal saldo;
    private String tipo;
}
