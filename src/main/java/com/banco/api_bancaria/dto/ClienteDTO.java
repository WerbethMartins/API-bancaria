package com.banco.api_bancaria.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private String nome;
    private String cpf;
    private String email;
}
