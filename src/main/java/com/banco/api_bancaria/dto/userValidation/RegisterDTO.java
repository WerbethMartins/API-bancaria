package com.banco.api_bancaria.dto.userValidation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "O nome de usuário é obrigatório!")
    @Size(min = 3, max = 20, message = "O nome de usuário deve conter 3-20 caracteres!")
    private String username;

    @NotBlank(message = "Email é obrigatório!")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "A senha deve conter pelo menos 8 caracteres, incluindo letra maiúscula, minúscula e número.")
    private String password;
}
