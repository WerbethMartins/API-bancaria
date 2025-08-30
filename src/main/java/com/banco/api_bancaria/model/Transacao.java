package com.banco.api_bancaria.model;

import com.banco.api_bancaria.enums.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private BigDecimal valor;

    @JsonFormat(pattern = "yyyy-MM-dd:T:HH:mm:ss" )
    private LocalDateTime dataHora;

    @ManyToOne
    private ContaBancaria contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    private ContaBancaria contaDestino;

    public Transacao(TipoTransacao tipo, BigDecimal valor, ContaBancaria contaOrigem, ContaBancaria contaDestino) {
        this.tipo = tipo;
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.dataHora = LocalDateTime.now();
    }
}
