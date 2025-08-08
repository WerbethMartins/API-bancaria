package com.banco.api_bancaria.service;

import ch.qos.logback.core.net.server.Client;
import com.banco.api_bancaria.dto.ClienteDTO;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;
import com.banco.api_bancaria.repository.ClienteRepository;
import com.banco.api_bancaria.repository.ContaBancariaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaBancariaRepository contaRepository;

    public ClienteService(ClienteRepository clienteRepository, ContaBancariaRepository contaRepository) {
        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }

    public List<Cliente> listarClientes(){
        return clienteRepository.findAll();
    }

    public Cliente abrirContaCliente(Long clienteId, String tipoConta){
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if(clienteOpt.isEmpty()){
            throw  new RuntimeException("Cliente não encontrado!");
        }

        Cliente cliente = clienteOpt.get();

        ContaBancaria conta = new ContaBancaria();
        conta.setNumero(String.valueOf(System.currentTimeMillis()));
        conta.setSaldo(BigDecimal.ZERO);
        conta.setTipo(tipoConta);
        conta.setCliente(cliente);

        cliente.getContas().add(conta);

        return clienteRepository.save(cliente);

    }

    public BigDecimal consultarSaldo(String numeroConta){
        Optional<ContaBancaria> contaOpt = contaRepository.findByNumero(numeroConta);
        if(contaOpt.isEmpty()){
            throw new RuntimeException("Conta não encontrada");
        }
        return contaOpt.get().getSaldo();
    }
}
