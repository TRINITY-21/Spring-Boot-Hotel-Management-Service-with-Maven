package com.hotel.hotel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RoomDto {

    //mapping our ROOM model obj to receive/send
    private Long id;

    private String name;

    private String description;

    private String room;

    private String image;

    private Double price;

    private String type;

    private boolean is_booked;

    private List<BookingDto> bookings;
}