package com.thumbex.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCredentials implements Serializable {

    private static final long serialVersionUID = 1;
    private String username;
    private String password;

}
