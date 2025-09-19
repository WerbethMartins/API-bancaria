package com.banco.api_bancaria.service;

import com.banco.api_bancaria.dto.modelDTO.ClienteDTO;
import com.banco.api_bancaria.dto.ClienteResponseDTO;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;
import com.banco.api_bancaria.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

public class ClientServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // Inicializa os mock
    }

    @Test
    void deveCriarClienteComSucesso(){
        // Arrange (Configuração do teste)
        ClienteDTO dto = new ClienteDTO();
        dto.setNome("João");
        dto.setCpf("12345678900");
        dto.setEmail("joao@email.com");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João");
        clienteSalvo.setCpf("12345678900");
        clienteSalvo.setEmail("joao@email.com");
        clienteSalvo.setContas(Collections.emptyList());

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act (Execução do teste)
        ClienteResponseDTO response = clienteService.criarCliente(dto);


        // Assert (Verificação do resultado)
        assertEquals("João", response.getNome());
        assertEquals("12345678900", response.getCpf());
        assertEquals("joao@email.com", response.getEmail());

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveCriarContaParaClienteExistente(){
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setContas(new ArrayList<>());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            // Simula que o número do cliente foi gerado
            c.getContas().get(0).setNumero(UUID.randomUUID().toString());
            return c;
        });

        // Act
        ClienteResponseDTO clienteComConta = clienteService.abrirContaCliente(1L, "Corrente");

        // Assert
        assertFalse(clienteComConta.getContas().isEmpty(), "Cliente deveria ter uma conta criada!");
        assertEquals("Corrente", clienteComConta.getContas().get(0).getTipo(), "Tipo de conta incorreto");
        assertEquals(BigDecimal.ZERO, clienteComConta.getContas().get(0).getSaldo(), "Saldo inicial deveria ser zero");
        assertNotNull(clienteComConta.getContas().get(0).getNumero(), "Número da conta deveria ser gerado");
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistir(){
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act e Assert
        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.abrirContaCliente(1L, "Poupação");
        });
    }

    @Test
    void develancarExcecaoQuandoCpfJaExistir(){
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setNome("Maria");
        dto.setCpf("12345678900");
        dto.setEmail("maria@email.com");

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNome("Maria");
        clienteExistente.setCpf("12345678900");

        when(clienteRepository.findByCpf("12345678900"))
                .thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.criarCliente(dto);
        });

        assertEquals("Já existe um cliente com esse cpf.", ex.getLocalizedMessage());
        verify(clienteRepository, never()).save(any()); // Garante que não salvou
    }

    @Test
    void deveRetornarSaldoSeExistir(){
        // Arrange
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("1500.00"));
        conta.setTipo("Corrente");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        // Act
        BigDecimal saldo = clienteService.consultarSaldo("123456");

        // Assert
        assertNotNull(saldo, "Saldo não pode ser nulo");
        assertEquals(new BigDecimal("1500.00"), saldo, "Saldo retornado está incorreto!");
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistir(){
        // Arrange
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());

        // Act e Assert
        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.consultarSaldo("999999");
        });
    }

    @Test
    void deveReornarListaDeCliente(){
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNome("João");
        cliente1.setCpf("12345678900");
        cliente1.setEmail("joao@email.com");
        cliente1.setContas(new ArrayList<>());

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Maria");
        cliente2.setCpf("98765432100");
        cliente2.setEmail("maria@email.com");
        cliente2.setContas(new ArrayList<>());

        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));

        // Act
        List<ClienteResponseDTO> clientes = clienteService.listarClientes();

        // Assert
        assertNotNull(clientes, "Lista de clientes não pode ser nula");
        assertEquals(2, clientes.size(), "Deveria retornar dois clientes");

        assertEquals("João", clientes.get(0).getNome());
        assertEquals("Maria", clientes.get(1).getNome());
    }
}
