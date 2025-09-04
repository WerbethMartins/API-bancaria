package com.banco.api_bancaria.controller;

import com.banco.api_bancaria.dto.ExtratoCompactoDTO;
import com.banco.api_bancaria.dto.ExtratoDTO;
import com.banco.api_bancaria.model.Transacao;
import com.banco.api_bancaria.service.ContaBancariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final ContaBancariaService contaBancariaService;

    @Autowired
    public TransacaoController(ContaBancariaService contaBancariaService){
        this.contaBancariaService = contaBancariaService;
    }

    @GetMapping("/{numeroConta}/extrato")
    public ResponseEntity<List<ExtratoDTO>> consultarExtrato(@PathVariable String numeroConta){
        List<ExtratoDTO> extrato = contaBancariaService.consultarExtrato(numeroConta)
                .stream()
                .map(ExtratoDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(extrato);
    }

    @GetMapping("/{numeroConta}/extrato/compacto")
    public ResponseEntity<List<ExtratoCompactoDTO>> consultarExtratoCompacto(@PathVariable String numeroConta){
        List<ExtratoCompactoDTO> extraCompacto = contaBancariaService.consultarExtrato(numeroConta)
                .stream()
                .map(transacao -> ExtratoCompactoDTO.fromEntity(transacao, numeroConta))
                .toList();
        return ResponseEntity.ok(extraCompacto);
    }
}
