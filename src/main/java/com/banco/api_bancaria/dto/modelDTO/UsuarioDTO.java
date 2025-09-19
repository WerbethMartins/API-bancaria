package com.banco.api_bancaria.dto.modelDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioDTO {
    private Long id;

    @NotBlank(message = "Informar o usuário é obrigatório.")
    @Size(min = 3, max = 50, message = "O nome de usuário deve conter de 3 a 50")
    private String username;

    @NotBlank(message = "Email é obrigatório!")
    @Email(message = "O email deve ser válido.")
    private String email;

    private List<String> roles;

    public UsuarioDTO(Long id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
