package com.hotel.hotel.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest {

    private String email;

    @Size(min = 6, max = 16, message = "Password is too short")
    private String password;

    @Size(min = 6, max = 16, message = "Password is too short")
    private String password2;
}
