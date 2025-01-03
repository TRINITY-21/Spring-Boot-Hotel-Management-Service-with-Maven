package com.hotel.hotel.service.impl;

import com.hotel.hotel.dto.RoomDto;
import com.hotel.hotel.mapper.RoomMapper;
import com.hotel.hotel.model.Room;
import com.hotel.hotel.model.UserRole;
import com.hotel.hotel.dto.UserDto;
import com.hotel.hotel.exception.BaseException;
import com.hotel.hotel.mapper.UserMapper;
import com.hotel.hotel.model.User;
import com.hotel.hotel.repository.UserRepository;
import com.hotel.hotel.request.LoginRequest;
import com.hotel.hotel.request.PasswordResetRequest;
import com.hotel.hotel.response.ResponseData;
import com.hotel.hotel.service.AwsS3Service;
import com.hotel.hotel.service.MailService;
import com.hotel.hotel.service.interfaces.IUserService;
import com.hotel.hotel.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AwsS3Service awsS3Service;

    private final MailService mailService;

    @Override
    public ResponseData register(MultipartFile profileImage, String username, String password, String city, String address,
                                 String phone, String email, UserRole userRole)
    {
        ResponseData responseData = new ResponseData();
        User user = new User();

        try{
            if(email == null || userRole == null || username == null || password == null){
                user.setRole(UserRole.USER.toString());
            }

            if(userRepository.existsByEmail(email)){
                throw new BaseException(email + "Already Exists");
            }

            String imageUrl = awsS3Service.saveImageToS3(profileImage);
            user.setProfileImage(imageUrl);
            user.setUsername(username);
            user.setAddress(address);
            user.setPhone(phone);
            user.setEmail(email);
            user.setCity(city);
            user.setPassword(passwordEncoder.encode(password));

            user.setActive(false);
            user.setActivationCode(UUID.randomUUID().toString());

            System.out.println(user );
            User savedUser = userRepository.save(user);

            //send response to user
            UserDto userDto = UserMapper.mapUserEntityToUserDto(savedUser);
            responseData.setStatusCode(200);
            responseData.setUser(userDto);

            System.out.println(user.getActivationCode() + "Activation code");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("firstName", user.getEmail());
            attributes.put("activationCode", "activate/" + user.getActivationCode());
            mailService.sendMessageHtml(user.getEmail(), "Account Verification", "registration-template", attributes);

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during registration" + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData login(LoginRequest loginRequest) throws BaseException {
        ResponseData responseData = new ResponseData();

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()));

            var user = userRepository.findByEmail(loginRequest.getEmail());
            System.out.println(user.isActive() + "Users are hereeee");
            if(!user.isActive()){
                responseData.setMessage("User not verified");
                responseData.setStatusCode(400);
                return responseData;
            }

            //if found, create token
            var token = jwtUtils.generateToken(user);
            responseData.setStatusCode(200);
            responseData.setToken(token);
            responseData.setRole(user.getRole());
            responseData.setExpiresIn("7 Days");
            responseData.setMessage("Success");


        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during login" + e.getMessage());
        }
        return responseData;


    }

    @Override
    public ResponseData getUserBookingHistory(String userId) {
        ResponseData responseData = new ResponseData();

        try{
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new BaseException("User not found"));

            UserDto userDto = UserMapper.mapUserEntityToUserDtoPlusUserBookingsAndRoom(user);
            responseData.setStatusCode(200);
            responseData.setUser(userDto);
            responseData.setMessage("Success");

    }catch (BaseException e){
        responseData.setStatusCode(400);
        responseData.setMessage(e.getMessage());
    }catch (Exception e){
        responseData.setStatusCode(500);
        responseData.setMessage("Error during get user booking history" + e.getMessage());
    }
        return responseData;
    }

    @Override
    public ResponseData getAllUsers() {
        ResponseData responseData = new ResponseData();

        try{
            List<User> userList = userRepository.findAll();
            List<UserDto> userDtoList =UserMapper.mapUserListEntityToUserListDTO(userList);

            responseData.setStatusCode(200);
            responseData.setMessage("Success");
            responseData.setUserList(userDtoList);

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during get all users" + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData deleteUser(String userId) {
            ResponseData responseData = new ResponseData();
        try{
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new BaseException("User not found"));

//            userRepository.deleteById(Long.valueOf(userId));
            user.set_deleted(true);
            userRepository.save(user);
            responseData.setStatusCode(200);
            responseData.setMessage("User Deleted Successfully");

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during deletion" + e.getMessage());
        }
        return responseData;
    }


    @Override
    public ResponseData getUserById(String userId) {
        ResponseData responseData = new ResponseData();
        try{
           User user =  userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new BaseException("User not found"));

            UserDto userDto = UserMapper.mapUserEntityToUserDto(user);

            responseData.setStatusCode(200);
            responseData.setUser(userDto);
            responseData.setMessage("User Deleted Successfully");

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during get user by id" + e.getMessage());
        }
        return responseData;
    }


    @Override
    public ResponseData getUserInfo(String email) {
        ResponseData responseData = new ResponseData();
        try{
            User user =  userRepository.findByEmail((email));
            if(user == null){
                throw new BaseException("User not found");
            }

            UserDto userDto = UserMapper.mapUserEntityToUserDto(user);

            responseData.setStatusCode(200);
            responseData.setUser(userDto);
            responseData.setMessage("Success");

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during get user info" + e.getMessage());
        }
        return responseData;
    }



    @Override
    public ResponseData getEmailByPasswordResetCode(String code){
        ResponseData responseData = new ResponseData();
        String email=  userRepository.getEmailByPasswordResetCode(code);
        System.out.println(email + "User mail");
        if(email == null){
            throw new BaseException("User not found");
        }

        User user = userRepository.findByEmail(email);

        UserDto userDto = UserMapper.mapUserEntityToUserDto(user);

        responseData.setStatusCode(200);
        responseData.setUser(userDto);
        responseData.setMessage("Success");

        return responseData;
    }

    @Override
    public ResponseData activateEmailCode(String code) {
        ResponseData responseData = new ResponseData();

        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            responseData.setStatusCode(400);
            responseData.setMessage("Activation code not found");
        }

        assert user != null;
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        responseData.setStatusCode(200);
        responseData.setMessage("User Activated Successfully");
        return responseData;
    }


    @Override
    public ResponseData sendPasswordResetCode(String email) {
        System.out.println(email + "userr");

        User user = userRepository.findByEmail(email);
        ResponseData responseData = new ResponseData();

        if (user == null) {
            responseData.setStatusCode(400);
            responseData.setMessage("User not found");        }
        assert user != null;
        user.setPasswordResetCode(UUID.randomUUID().toString());
        userRepository.save(user);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", user.getEmail());
        attributes.put("resetCode", "reset-password/" + user.getPasswordResetCode());
        mailService.sendMessageHtml(user.getEmail(), "Password reset", "password-reset-template", attributes);
        responseData.setStatusCode(200);
        responseData.setMessage("Password Reset Code sent Successfully");
        return responseData;
    }





    @Override
    public ResponseData resetPassword(PasswordResetRequest request) {
        ResponseData responseData = new ResponseData();

        // Check if passwords match
        if (request.getPassword() == null || !request.getPassword().equals(request.getPassword2())) {
            responseData.setStatusCode(400);
            responseData.setMessage("Passwords do not match");
            return responseData;
        }

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            responseData.setStatusCode(400);
            responseData.setMessage("User not found");
            return responseData;
        }

        // Validate password reset code
        if (user.getPasswordResetCode() == null) {
            responseData.setStatusCode(400);
            responseData.setMessage("Invalid or expired reset code");
            return responseData;
        }

        // Update password and clear reset code
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordResetCode(null);

        // Save updated user to database
        userRepository.save(user);

        responseData.setStatusCode(200);
        responseData.setMessage("Password reset successfully");
        return responseData;
    }


    @Override
    public ResponseData addUser(MultipartFile profileImage, String username, String password, String city, String address,
                                 String phone, String email, UserRole userRole)
    {
        ResponseData responseData = new ResponseData();
        User user = new User();

        try{
            if(email == null || userRole == null || username == null || password == null){
                user.setRole(UserRole.USER.toString());
            }

            if(userRepository.existsByEmail(email)){
                throw new BaseException(email + "Already Exists");
            }

            String imageUrl = awsS3Service.saveImageToS3(profileImage);
            user.setProfileImage(imageUrl);
            user.setUsername(username);
            user.setAddress(address);
            user.setPhone(phone);
            user.setEmail(email);
            user.setCity(city);
            user.setPassword(passwordEncoder.encode(password));

            user.setActive(false);
            user.setActivationCode(UUID.randomUUID().toString());

            User savedUser = userRepository.save(user);

            //send response to user
            UserDto userDto = UserMapper.mapUserEntityToUserDto(savedUser);
            responseData.setStatusCode(200);
            responseData.setUser(userDto);

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("firstName", user.getEmail());
            attributes.put("activationCode", "api/auth/activate/" + user.getActivationCode());
            mailService.sendMessageHtml(user.getEmail(), "Account Verification", "registration-template", attributes);

        }catch (BaseException e){
            responseData.setStatusCode(400);
            responseData.setMessage(e.getMessage());
        }catch (Exception e){
            responseData.setStatusCode(500);
            responseData.setMessage("Error during registration" + e.getMessage());
        }
        return responseData;
    }



    @Override
    public ResponseData updateUser(Long userId,MultipartFile profileImage,String username, String email, String city, String address,String phone) {
        ResponseData response = new ResponseData();

        try {
            String imageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(profileImage);
            }
            User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("User Not Found"));
            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (phone != null) user.setPhone(phone);
            if (address != null) user.setAddress(address);
            if (city != null) user.setCity(city);
            if (imageUrl != null) user.setProfileImage(imageUrl);


            User updatedUser = userRepository.save(user);
            UserDto userDTO = UserMapper.mapUserEntityToUserDto(updatedUser);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Errors updating user " + e.getMessage());
        }
        return response;
    }


}

