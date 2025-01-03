package com.hotel.hotel.service.interfaces;

import com.hotel.hotel.model.User;
import com.hotel.hotel.response.ResponseData;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {

    ResponseData addNewRoom(MultipartFile image, String type, String name, Double price, String description);

    List<String> getAllRoomTypes();

    ResponseData getAllRooms();

    ResponseData deleteRoom(Long roomId);

    ResponseData updateRoom(Long roomId, String description,String name, String roomType, Double roomPrice, MultipartFile photo);

    ResponseData getRoomById(Long roomId);

    ResponseData getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    ResponseData getAllAvailableRooms();
}

