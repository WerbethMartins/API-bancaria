package com.banco.api_bancaria.dto.userValidation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public JwtResponse(String token, String refreshToken, String username,List<String> roles){
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = type;
        this.username = username;
        this.roles = roles;
    }
}
