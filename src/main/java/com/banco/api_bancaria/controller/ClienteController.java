package com.banco.api_bancaria.controller;

import com.banco.api_bancaria.dto.ClienteDTO;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;
import com.banco.api_bancaria.repository.ClienteRepository;
import com.banco.api_bancaria.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @PostMapping("/abrir-conta/{clienteId}")
    public ResponseEntity<Cliente> abriConta(@PathVariable Long clienteId, @RequestParam String tipoConta){
        Cliente clienteAtualizado = clienteService.abrirContaCliente(clienteId, tipoConta);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @GetMapping("/saldo/{numeroConta}")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable String numeroConta){
        BigDecimal saldo = clienteService.consultarSaldo(numeroConta);
        return ResponseEntity.ok(saldo);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        return ResponseEntity.ok(clienteService.listarClientes());
    }

}
