package com.hotel.hotel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

//ignoring null values
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserDto {

    //mapping our USER model obj to receive/send
    private Long id;

    private String username;

    private String phone;

    private String email;

    private String role;

    private String address;

    private String city;

    private String profileImage;

    private boolean is_deleted;

    private boolean isActive;

    private List<BookingDto> bookings;
}
