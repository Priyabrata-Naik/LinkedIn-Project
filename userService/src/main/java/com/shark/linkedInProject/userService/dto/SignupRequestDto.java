package com.shark.linkedInProject.userService.dto;

import lombok.Data;

@Data
public class SignupRequestDto {

    private String email;

    private String password;

    private String name;

}
