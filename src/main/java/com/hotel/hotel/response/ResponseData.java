package com.hotel.hotel.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel.hotel.dto.BookingDto;
import com.hotel.hotel.dto.RoomDto;
import com.hotel.hotel.dto.UserDto;
import com.hotel.hotel.model.Booking;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {

    //mapping our response obj to return
    private int statusCode;

    private String message;

    private String token;

    private String role;

    private String expiresIn;

    private String bookingConfirmationCode;

    private UserDto user;

    private RoomDto room;

    private BookingDto booking;

    private List<UserDto> userList;

    private List<RoomDto> roomList;

    private List<BookingDto> bookingList;

}
