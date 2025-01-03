package com.hotel.hotel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BookingDto {

    //mapping our BOOKING model obj to receive/send
    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int numberOfAdults;

    private int numberOfChildren;

    private int totalNumberOfGuests;

    private String bookingConfirmationCode;

    private UserDto user;

    private RoomDto room;

}
