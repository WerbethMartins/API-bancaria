package com.banco.api_bancaria.service;

import com.banco.api_bancaria.dto.TransferenciaDTO;
import com.banco.api_bancaria.dto.TransferenciaResponseDTO;
import com.banco.api_bancaria.model.Cliente;
import com.banco.api_bancaria.model.ContaBancaria;
import com.banco.api_bancaria.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ContaBancariaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ContaBancariaService contaService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveDepositarComSucesso(){
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("1000"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        contaService.depositar("123456", new BigDecimal("500"));

        assertEquals(new BigDecimal("1500"), conta.getSaldo());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void deveLancarExcecaoQuandoDepositoForZeroOuNegativo(){
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("1000"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));


        // Deposito zero
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            contaService.depositar("123456", BigDecimal.ZERO);
        });

        assertEquals("O valor do depósito deve ser maior que zero", ex1.getMessage());

        // Depósito negativo
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            contaService.depositar("123456", BigDecimal.valueOf(-50));
        });

        assertEquals("O valor do depósito deve ser maior que zero", ex2.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistirNoDeposito() {
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> contaService.depositar("999999", BigDecimal.valueOf(100)));
    }

    @Test
    void deveSacarComSucesso(){
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("1000"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        contaService.sacar("123456", new BigDecimal("500"));

        assertEquals(new BigDecimal("500"), conta.getSaldo());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void deveLancarExcecaoQuandoSaqueForZeroOuNegativo(){
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("1000"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        // Saque zero
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
           contaService.sacar("123456", BigDecimal.ZERO);
        });

        assertEquals("O valor para saque deve ser maior que zero", ex1.getMessage());

        // Saque negativo
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
           contaService.sacar("123456", BigDecimal.valueOf(-50));
        });

        assertEquals("O valor para saque deve ser maior que zero", ex2.getMessage());
    }

    @Test
    void deveLancarExcecaoParaSaldoInsuficiente(){
        ContaBancaria conta = new ContaBancaria();
        conta.setNumero("123456");
        conta.setSaldo(new BigDecimal("300"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setContas(new ArrayList<>());
        cliente.getContas().add(conta);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            contaService.sacar("123456", new BigDecimal("500"));
        });

        assertEquals("Saldo insuficiente para realizar o saque", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistirNoSaque() {
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> contaService.sacar("999999", BigDecimal.valueOf(100)));
    }

    @Test
    void deveTransferirComSucesso() {
        // Arrange
        ContaBancaria contaOrigem = new ContaBancaria();
        contaOrigem.setNumero("111111");
        contaOrigem.setSaldo(new BigDecimal("1000"));

        ContaBancaria contaDestino = new ContaBancaria();
        contaDestino.setNumero("222222");
        contaDestino.setSaldo(new BigDecimal("500"));

        Cliente clienteOrigem = new Cliente();
        clienteOrigem.setId(1L);
        clienteOrigem.setContas(new ArrayList<>());
        clienteOrigem.getContas().add(contaOrigem);

        Cliente clienteDestino = new Cliente();
        clienteDestino.setId(2L);
        clienteDestino.setContas(new ArrayList<>());
        clienteDestino.getContas().add(contaDestino);

        when(clienteRepository.findAll()).thenReturn(List.of(clienteOrigem, clienteDestino));
        when(clienteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaDestino("111111");
        dto.setNumeroContaOrigem("222222");
        dto.setValor(new BigDecimal("300"));

        TransferenciaResponseDTO responseDTO = contaService.transferir(dto);

        // Assert
        assertEquals(new BigDecimal("700"), responseDTO.getSaldoContaOrigem());
        assertEquals(new BigDecimal("700"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("800"), contaDestino.getSaldo());
        verify(clienteRepository, times(2)).save(any());
    }

    @Test
    void deveLancarExcecaoQuandTransferenciaForZeroOuNegativa(){
        ContaBancaria contaOrigem = new ContaBancaria();
        contaOrigem.setNumero("111111");
        contaOrigem.setSaldo(new BigDecimal("1000"));

        ContaBancaria contaDestino = new ContaBancaria();
        contaDestino.setNumero("222222");
        contaDestino.setSaldo(new BigDecimal("500"));

        Cliente clienteOrigem = new Cliente();
        clienteOrigem.setId(1L);
        clienteOrigem.setContas(new ArrayList<>());
        clienteOrigem.getContas().add(contaOrigem);

        Cliente clienteDestino = new Cliente();
        clienteDestino.setId(2L);
        clienteDestino.setContas(new ArrayList<>());
        clienteDestino.getContas().add(contaDestino);

        when(clienteRepository.findAll()).thenReturn(List.of(clienteOrigem, clienteDestino));

        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("111111");
        dto.setNumeroContaDestino("222222");
        dto.setValor(BigDecimal.ZERO);

        // Transferência zero
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> contaService.transferir(dto));
        assertEquals("O valor deve ser maior que zero para realizar a transferência", ex1.getMessage());

        TransferenciaDTO dtoNeg = new TransferenciaDTO();
        dtoNeg.setNumeroContaOrigem("111111");
        dtoNeg.setNumeroContaDestino("222222");
        dtoNeg.setValor(new BigDecimal("-200"));

        // Transferência negativa
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> contaService.transferir(dtoNeg));
        assertEquals("O valor deve ser maior que zero para realizare a transferência", ex2.getMessage());

        assertEquals("O valor deve ser maior que zero para realizar a transferência", ex2.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoContasForemIguais() {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("111111");
        dto.setNumeroContaDestino("111111");
        dto.setValor(new BigDecimal("100"));
    }

    @Test
    void deveLancarExcecaoParaSaldoInsuficienteParaTransferir() {
        ContaBancaria contaOrigem = new ContaBancaria();
        contaOrigem.setNumero("111111");
        contaOrigem.setSaldo(new BigDecimal("200"));

        ContaBancaria contaDestino = new ContaBancaria();
        contaDestino.setNumero("222222");
        contaDestino.setSaldo(new BigDecimal("500"));

        Cliente clienteOrigem = new Cliente();
        clienteOrigem.setId(1L);
        clienteOrigem.setContas(new ArrayList<>());
        clienteOrigem.getContas().add(contaOrigem);

        Cliente clienteDestino = new Cliente();
        clienteDestino.setId(2L);
        clienteDestino.setContas(new ArrayList<>());
        clienteDestino.getContas().add(contaDestino);

        when(clienteRepository.findAll()).thenReturn(List.of(clienteOrigem, clienteDestino));

        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("111111");
        dto.setNumeroContaDestino("222222");
        dto.setValor(new BigDecimal("500"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contaService.transferir(dto));
        assertEquals("Saldo insuficiente para realizar a transferência", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistirNaTransferencia() {
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());

        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("999999");
        dto.setNumeroContaDestino("888888");
        dto.setValor(new BigDecimal("100"));

        assertThrows(EntityNotFoundException.class, () -> contaService.transferir(dto));
    }


}
