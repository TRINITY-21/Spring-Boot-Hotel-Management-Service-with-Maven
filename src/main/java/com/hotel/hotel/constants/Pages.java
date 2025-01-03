package com.hotel.hotel.constants;

public class Pages {

    // Base API path, added separately in controllers
    public static final String API_BASE = "/api";

    // AUTH
    public static final String AUTH_BASE = "/api/auth";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String ACTIVATE_EMAIL = "/activate/{code}";
    public static final String FORGOT_PASSWORD = "/forgot";
    public static final String RESET_PASSWORD = "/reset/{code}";


    // USER
    public static final String USERS_BASE = "/api/users";
    public static final String ADD_USER = "/api/add/user";
    public static final String ALL_USERS = "/all";
    public static final String GET_USER_BY_ID = "/get-by-id/{userId}";
    public static final String DELETE_USER = "/delete/{userId}";
    public static final String GET_LOGGED_IN_USER_INFO = "/get-logged-in-profile-info";
    public static final String GET_USER_BOOKINGS = "/get-user-bookings/{userId}";
    public static final String UPDATE_USER_DETAILS = "/edit-user/{userId}";

    // ROOMS
    public static final String ROOMS_BASE = "/api/rooms";
    public static final String ADD_ROOM = "/add";
    public static final String GET_ALL_ROOMS = "/all";
    public static final String GET_ROOM_TYPES = "/types";
    public static final String GET_ROOM_BY_ID = "/room-by-id/{roomId}";
    public static final String GET_ALL_AVAILABLE_ROOMS = "/all-available-rooms";
    public static final String GET_AVAILABLE_ROOMS_BY_DATE_AND_TYPE = "/available-rooms-by-date-and-type";
    public static final String UPDATE_ROOM = "/update/{roomId}";
    public static final String DELETE_ROOM = "/delete/{roomId}";

    // BOOKINGS
    public static final String BOOKINGS_BASE = "/api/bookings";
    public static final String ADD_BOOKING = "/book-room/{roomId}/{userId}";
    public static final String GET_ALL_BOOKINGS = "/all";
    public static final String GET_BOOKING_BY_CONFIRMATION_CODE = "/get-by-confirmation-code/{confirmationCode}";
    public static final String CANCEL_BOOKING = "/cancel/{bookingId}";

    // NOT FOUND
    public static final String PAGE_NOT_FOUND = "/not-found";
}
