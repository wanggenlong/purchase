package com.purchase.vo;

import lombok.Data;

@Data
public class LoginResponseVO {

    private String token;
    private String username;
    private String realName;

}