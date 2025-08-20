package com.banco.api_bancaria.dto;

import java.math.BigDecimal;

public class OperacaoDTO {
    private String numeroConta;
    private BigDecimal valor;

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
