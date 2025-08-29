package com.banco.api_bancaria.exception;

import com.banco.api_bancaria.dto.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.naming.Binding;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarBadRequestParaIllegalArgumentException(){
        IllegalArgumentException ex = new IllegalArgumentException();

        ResponseEntity<ErrorResponseDTO> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Valor inválido", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
     @Test
    void deveRetornarNotFoundParaEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Conta não encontrada");

        ResponseEntity<ErrorResponseDTO> response = handler.handlerEntityNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conta não encontrada", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void deveRetornarInternalServerErrorParaExcecaoGenerica() {
        Exception ex = new Exception("Falha inesperada");

        ResponseEntity<ErrorResponseDTO> response = handler.handlerGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Falha inesperada", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void deveRetornarErroDeValidacao(){
        // Simula um objeto inválido com o erro no campo "nome"
        Object target = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.rejectValue("nome", "NotNull", "O nome é obrigatório");

        // Cria exceção simulada
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Executa
        ResponseEntity<ErrorResponseDTO> response = handler.handlerValidationExceptions(ex);

        // Verificações
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("nome"));
        assertTrue(response.getBody().getMessage().contains("O nome é obrigatório"));
        assertNotNull(response.getBody().getTimestamp());
    }
}
