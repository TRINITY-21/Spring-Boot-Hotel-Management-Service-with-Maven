package com.hotel.hotel.controller;

import com.hotel.hotel.constants.Pages;
import com.hotel.hotel.model.Booking;
import com.hotel.hotel.response.ResponseData;
import com.hotel.hotel.service.interfaces.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:7070/")
@RequestMapping(Pages.BOOKINGS_BASE)

public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @PostMapping(Pages.ADD_BOOKING)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<ResponseData> saveBookings(@PathVariable Long roomId,
                                                     @PathVariable Long userId,
                                                     @RequestBody Booking bookingRequest) {

        ResponseData response = bookingService.saveBooking(roomId, userId, bookingRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping(Pages.GET_ALL_BOOKINGS)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> getAllBookings() {
        ResponseData response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping(Pages.GET_BOOKING_BY_CONFIRMATION_CODE)
    public ResponseEntity<ResponseData> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        ResponseData response = bookingService.findBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping(Pages.CANCEL_BOOKING)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<ResponseData> cancelBooking(@PathVariable Long bookingId) {
        ResponseData response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
