package com.hotel.hotel.mapper;

import com.hotel.hotel.dto.BookingDto;
import com.hotel.hotel.dto.RoomDto;
import com.hotel.hotel.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto mapBookingsEntityToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        bookingDto.setNumberOfAdults(booking.getNumberOfAdults());
        bookingDto.setNumberOfChildren(booking.getNumberOfChildren());
        bookingDto.setCheckInDate(booking.getCheckInDate());
        bookingDto.setCheckOutDate(booking.getCheckOutDate());
        bookingDto.setTotalNumberOfGuests(booking.getTotalNumberOfGuests());

        return bookingDto;
    }


    //mapUser -> if user should be mapped or not
    public static BookingDto mapBookingEntityToBookingDTOPlusBookedRooms(Booking booking, boolean mapUser) {

        BookingDto bookingDTO = new BookingDto();

        // Map simple fields
        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumberOfAdults(booking.getNumberOfAdults());
        bookingDTO.setNumberOfChildren(booking.getNumberOfChildren());
        bookingDTO.setTotalNumberOfGuests(booking.getTotalNumberOfGuests());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        if (mapUser) {
            bookingDTO.setUser(UserMapper.mapUserEntityToUserDto(booking.getUser()));
        }
        if (booking.getRoom() != null) {
            RoomDto roomDTO = new RoomDto();

            roomDTO.setId(booking.getRoom().getId());
            roomDTO.setType(booking.getRoom().getType());
            roomDTO.setPrice(booking.getRoom().getPrice());
            roomDTO.setImage(booking.getRoom().getImage());
            roomDTO.setDescription(booking.getRoom().getDescription());
            bookingDTO.setRoom(roomDTO);
        }
        return bookingDTO;
    }



    public static List<BookingDto> mapBookingListEntityToBookingListDTO(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::mapBookingsEntityToBookingDto).collect(Collectors.toList());
    }

}
