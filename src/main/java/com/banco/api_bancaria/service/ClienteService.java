package com.banco.api_bancaria.service;

import com.banco.api_bancaria.dto.modelDTO.ClienteDTO;
import com.banco.api_bancaria.dto.ClienteResponseDTO;
import com.banco.api_bancaria.dto.ContaResponseDTO;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;
import com.banco.api_bancaria.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponseDTO> listarClientes(){
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::converteClienteResponseDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO criarCliente(ClienteDTO dto){
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEmail(dto.getEmail());

        Cliente salvo = clienteRepository.save(cliente);

        return converteClienteResponseDTO(salvo);
    }

    public ClienteResponseDTO abrirContaCliente(Long clienteId, String tipoConta){
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado!"));

        ContaBancaria conta = new ContaBancaria();

        conta.setNumero(gerarNumeroConta());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setTipo(tipoConta);
        conta.setCliente(cliente);

        cliente.getContas().add(conta);

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return converteClienteResponseDTO(clienteAtualizado);

    }

    public BigDecimal consultarSaldo(String numeroConta){
        return clienteRepository.findAll().stream()
                .flatMap(cliente -> cliente.getContas().stream())
                .filter(conta -> conta.getNumero().equals(numeroConta))
                .map(ContaBancaria::getSaldo)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
    }

    // Gerar número da conta
    private String gerarNumeroConta(){
        return String.valueOf(System.currentTimeMillis()).substring(5, 11);
    }

    @NotNull
    private ClienteResponseDTO converteClienteResponseDTO(Cliente cliente){
         List<ContaResponseDTO> contasDTO = cliente.getContas().stream()
            .map(conta -> new ContaResponseDTO(
                    conta.getNumero(),
                    conta.getSaldo(),
                    conta.getTipo()
            ))
            .collect(Collectors.toList());

        ClienteResponseDTO dto = new ClienteResponseDTO(
          cliente.getId(),
          cliente.getNome(),
          cliente.getCpf(),
          cliente.getEmail()
        );

        dto.setContas(contasDTO);

        return dto;
    }
}
