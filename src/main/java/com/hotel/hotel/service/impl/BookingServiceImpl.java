package com.hotel.hotel.service.impl;


import com.hotel.hotel.dto.BookingDto;
import com.hotel.hotel.exception.BaseException;
import com.hotel.hotel.mapper.BookingMapper;
import com.hotel.hotel.model.Booking;
import com.hotel.hotel.model.Room;
import com.hotel.hotel.model.User;
import com.hotel.hotel.repository.BookingRepository;
import com.hotel.hotel.repository.RoomRepository;
import com.hotel.hotel.repository.UserRepository;
import com.hotel.hotel.response.ResponseData;
import com.hotel.hotel.service.MailService;
import com.hotel.hotel.service.interfaces.IBookingService;
import com.hotel.hotel.service.interfaces.IRoomService;
import com.hotel.hotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    private final MailService mailService;

    @Override
    public ResponseData saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        ResponseData response = new ResponseData();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new BaseException("Room not Available for selected date range");
            }


            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            room.set_booked(true);
            roomRepository.save(room);

            //SEND MAIL
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("booking", bookingRequest);
            attributes.put("user", user);
            attributes.put("room", room);
            mailService.sendMessageHtml(user.getEmail(), "Booking Confirmation", "order-template", attributes);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());

        }
        return response;
    }


    @Override
    public ResponseData findBookingByConfirmationCode(String confirmationCode) {

        ResponseData response = new ResponseData();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new BaseException("Booking Not Found"));
            BookingDto bookingDTO = BookingMapper.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public ResponseData getAllBookings() {

        ResponseData response = new ResponseData();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDto> bookingDTOList = BookingMapper.mapBookingListEntityToBookingListDTO(bookingList);
            System.out.println(bookingDTOList+"all book");
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());

        }
        return response;
    }

    @Override
    public ResponseData cancelBooking(Long bookingId) {

        ResponseData response = new ResponseData();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new BaseException("Booking Does Not Exist"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
