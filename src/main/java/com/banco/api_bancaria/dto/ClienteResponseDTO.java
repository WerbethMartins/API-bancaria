package com.banco.api_bancaria.dto;

import com.banco.api_bancaria.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ClienteResponseDTO {
    private long id;
    private String nome;
    private String cpf;
    private String email;

    private List<ContaResponseDTO> contas;

    public ClienteResponseDTO(long id, String nome, String cpf, String email, List<ContaResponseDTO> contas){
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.contas = contas;
    }

    public ClienteResponseDTO(Long id, String nome, String cpf, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

}
