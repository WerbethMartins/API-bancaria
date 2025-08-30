package com.banco.api_bancaria.service;

import com.banco.api_bancaria.dto.*;
import com.banco.api_bancaria.enums.TipoTransacao;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;

import com.banco.api_bancaria.model.Transacao;
import com.banco.api_bancaria.repository.ClienteRepository;
import com.banco.api_bancaria.repository.ContaBancariaRepository;
import com.banco.api_bancaria.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaBancariaService {

    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaBancariaService(ClienteRepository clienteRepository, TransacaoRepository transacaoRepository) {
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public ContaBancaria depositar(String numeroConta, BigDecimal valor){
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O valor do déposito deve ser maior que zero");
        }

        // Busca o cliente que possui essa conta
        Cliente cliente = clienteRepository.findAll()
                .stream()
                .filter(c -> c.getContas()
                        .stream()
                        .anyMatch(conta -> conta.getNumero().equals(numeroConta)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada!"));

        // Localiza a conta e adiciona o valor
        ContaBancaria conta = cliente.getContas()
                .stream()
                .filter(c -> c.getNumero().equals(numeroConta))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada!"));

        conta.setSaldo(conta.getSaldo().add(valor));

        Transacao transacao = new Transacao(
            TipoTransacao.DEPOSITO,
            valor,
            null,
            conta
        );

        transacaoRepository.save(transacao);

        return conta;
    }

    public ContaBancaria sacar(String numeroConta, BigDecimal valor){
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O valor deve ser maior que zero!");
        }

        Cliente cliente = clienteRepository.findAll()
                .stream()
                .filter(c -> c.getContas()
                        .stream()
                        .anyMatch(conta -> conta.getNumero().equals(numeroConta)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada!"));

        ContaBancaria conta = cliente.getContas()
                .stream()
                .filter(c -> c.getNumero().equals(numeroConta))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contra não encontrada!"));

        // Verifica o saldo
        if(conta.getSaldo().compareTo(valor) < 0){
            throw  new IllegalArgumentException("Saldo insuficiente para realizar o saque!");
        }

        // Realiza o saque
        conta.setSaldo(conta.getSaldo().subtract(valor));

        Transacao transacao = new Transacao(
           TipoTransacao.SAQUE,
           valor,
           null,
           conta
        );

        transacaoRepository.save(transacao);

        return conta;
    }

    @Transactional
    public TransferenciaResponseDTO transferir(TransferenciaDTO dto){

        BigDecimal valor = dto.getValor();
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw  new IllegalArgumentException("O valor para transferir deve ser maior que zero");
        }

        if(dto.getNumeroContaOrigem() != null){
            if(dto.getNumeroContaOrigem().equals(dto.getNumeroContaDestino())){
                throw new IllegalArgumentException("A conta de origem e destino devem ser diferentes.");
            }

            Cliente clienteOrigem = clienteRepository.findAll()
                    .stream()
                    .filter(co -> co.getContas()
                            .stream()
                            .anyMatch(conta -> conta.getNumero().equals(dto.getNumeroContaOrigem())))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

            Cliente clienteDestino = clienteRepository.findAll()
                    .stream()
                    .filter(cd -> cd.getContas()
                            .stream()
                            .anyMatch(contaDestino -> contaDestino.getNumero().equals(dto.getNumeroContaDestino())))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Conta não econtrada!"));

            ContaBancaria contaOrigem = clienteOrigem.getContas()
                    .stream()
                    .filter(co -> co.getNumero().equals(dto.getNumeroContaOrigem()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Conta de origem não encontrada!"));

            ContaBancaria contaDestino = clienteDestino.getContas()
                    .stream()
                    .filter(cd -> cd.getNumero().equals(dto.getNumeroContaDestino()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Conta de destino não encontrada!"));

            // Verifica se o saldo é positivo
            if(contaOrigem.getSaldo().compareTo(valor) < 0){
                throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência");
            }

            // Realiza a transferência
            contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

            Transacao transacao = new Transacao(
                    TipoTransacao.TRANSFERENCIA,
                    valor,
                    contaOrigem,
                    contaDestino
            );

            transacaoRepository.save(transacao);

            return new TransferenciaResponseDTO(contaOrigem, contaDestino, valor);
        }

        return null;
    }

    public List<Transacao> consultarExtrato(String numeroConta){
        return transacaoRepository.findByContaOrigem_NumeroOrContaDestino_Numero(numeroConta, numeroConta)
                .stream()
                .filter(transacao -> transacao.getContaOrigem() != null && transacao.getContaDestino() != null)
                .toList();
    }
}
