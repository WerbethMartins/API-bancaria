package com.banco.api_bancaria.dto;

import com.banco.api_bancaria.model.ContaBancaria;

import java.math.BigDecimal;

public class TransferenciaResponseDTO {
    private String numeroContaOrigem;
    private BigDecimal saldoContaOrigem;
    private String numeroContaDestino;
    private BigDecimal saldoContaDestino;
    private BigDecimal valorTransferido;

    public TransferenciaResponseDTO(ContaBancaria contaOrigem, ContaBancaria contaDestino, BigDecimal valor) {
        this.numeroContaOrigem = contaOrigem.getNumero();
        this.saldoContaOrigem = contaOrigem.getSaldo();
        this.numeroContaDestino = contaDestino.getNumero();
        this.saldoContaDestino = contaDestino.getSaldo();
        this.valorTransferido = valor;
    }

    public String getNumeroContaOrigem() {
        return numeroContaOrigem;
    }

    public void setNumeroContaOrigem(String numeroContaOrigem) {
        this.numeroContaOrigem = numeroContaOrigem;
    }

    public BigDecimal getSaldoContaOrigem() {
        return saldoContaOrigem;
    }

    public void setSaldoContaOrigem(BigDecimal saldoContaOrigem) {
        this.saldoContaOrigem = saldoContaOrigem;
    }

    public String getNumeroContaDestino() {
        return numeroContaDestino;
    }

    public void setNumeroContaDestino(String numeroContaDestino) {
        this.numeroContaDestino = numeroContaDestino;
    }

    public BigDecimal getSaldoContaDestino() {
        return saldoContaDestino;
    }

    public void setSaldoContaDestino(BigDecimal saldoContaDestino) {
        this.saldoContaDestino = saldoContaDestino;
    }

    public BigDecimal getValorTransferido() {
        return valorTransferido;
    }

    public void setValorTransferido(BigDecimal valorTransferido) {
        this.valorTransferido = valorTransferido;
    }
}
