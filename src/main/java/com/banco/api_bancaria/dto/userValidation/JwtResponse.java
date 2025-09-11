package com.banco.api_bancaria.dto.userValidation;

public class JwtResponse {
    private String token;
    private String type = "Bearer";

    public JwtResponse(String token){
        this.token = token;
    }
}
