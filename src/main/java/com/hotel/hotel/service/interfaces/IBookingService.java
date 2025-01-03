package com.hotel.hotel.service.interfaces;

import com.hotel.hotel.model.Booking;
import com.hotel.hotel.response.ResponseData;

public interface IBookingService {

    ResponseData saveBooking(Long roomId, Long userId, Booking bookingRequest);

    ResponseData findBookingByConfirmationCode(String confirmationCode);

    ResponseData getAllBookings();

    ResponseData cancelBooking(Long bookingId);

}
