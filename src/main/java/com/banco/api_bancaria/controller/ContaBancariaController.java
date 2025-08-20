package com.banco.api_bancaria.controller;

import com.banco.api_bancaria.dto.ContaResponseDTO;
import com.banco.api_bancaria.dto.OperacaoDTO;
import com.banco.api_bancaria.dto.TransferenciaDTO;
import com.banco.api_bancaria.model.ContaBancaria;

import com.banco.api_bancaria.service.ContaBancariaService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/contas")
public class ContaBancariaController {

    private final ContaBancariaService contaService;

    @Autowired
    public ContaBancariaController(ContaBancariaService contaService){
        this.contaService = contaService;
    }

    @PostMapping("/depositar")
    public ResponseEntity<ContaResponseDTO> depositar(@Valid @RequestBody OperacaoDTO dto) {
        ContaBancaria conta = contaService.depositar(dto.getNumeroConta(), dto.getValor());

        ContaResponseDTO response = new ContaResponseDTO(
                conta.getNumero(),
                conta.getSaldo(),
                conta.getTipo()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sacar")
    public ResponseEntity<ContaResponseDTO> sacar(@Valid @RequestBody OperacaoDTO dto){
        ContaBancaria conta = contaService.sacar(dto.getNumeroConta(), dto.getValor());

        ContaResponseDTO response = new ContaResponseDTO(
                conta.getNumero(),
                conta.getSaldo(),
                conta.getTipo()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("transferir")
    public ResponseEntity<ContaResponseDTO> transferir(@Valid @RequestBody TransferenciaDTO dto){
        ContaBancaria conta = contaService.transferir(
                dto.getContaOrigem(),
                dto.getContaDestino(),
                dto.getValor());

        ContaResponseDTO response = new ContaResponseDTO(
            conta.getNumero(),
            conta.getSaldo(),
            conta.getTipo()
        );

        return ResponseEntity.ok(response);
    }
}
