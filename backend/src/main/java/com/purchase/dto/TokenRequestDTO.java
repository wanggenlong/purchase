package com.purchase.dto;

import lombok.Data;

@Data
public class TokenRequestDTO {

    /**
     * description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9..."
     */
    private String token;

}