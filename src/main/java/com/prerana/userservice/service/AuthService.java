package com.prerana.userservice.service;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.Role;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.exceptions.MobileNumberOTPNotVerified;
import com.prerana.userservice.exceptions.UserAlreadyExistException;
import com.prerana.userservice.repository.UserRepository;
import com.prerana.userservice.store.TempUserStore;
import com.prerana.userservice.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final JwtService jwt;


    @Autowired
    private  TempUserStore tempStore;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder encoder;
    // 1️⃣ SEND OTP
    public void sendOtp(String mobileNumber) {

        if (userRepository.findByMobileNumber(mobileNumber).isPresent()) {
            throw new UserAlreadyExistException();
        }

        otpService.generateAndSendOtp(mobileNumber);
    }

    // 2️⃣ VERIFY OTP
    public boolean verifyOtp(VerifyOtpRequestDto req) {
        return otpService.verifyOtp(req.getMobileNumber(), req.getOtp());
    }

    // 3️⃣ SET PASSWORD (TEMPORARY)
    public void setPassword(SetPasswordRequestDto req) {

        if (!otpService.isMobileVerified(req.getMobileNumber())) {
            throw new RuntimeException("Mobile number not OTP verified");
        }

        String encryptedPw = encoder.encode(req.getPassword());
        tempStore.storePassword(req.getMobileNumber(), encryptedPw);
    }

    // 4️⃣ FINAL SIGNUP
    public void signup(SignUpRequestDto req) {

        if (!otpService.isMobileVerified(req.getMobileNumber())) {
            throw new MobileNumberOTPNotVerified("Mobile number not OTP verified");
        }

        Optional<UserEntity> userByEmail = userRepository.findByEmail(req.getEmail());
        if(userByEmail.isPresent()){
            throw new UserAlreadyExistException("User already exists with this email");
        }
        String tempPassword = tempStore.getPassword(req.getMobileNumber());
        if (tempPassword == null) {
            throw new RuntimeException("Password not set yet");
        }

        UserEntity user = new UserEntity();

        user.setMobileNumber(req.getMobileNumber());
        user.setUserType(UserType.fromString(req.getUserType()));
        user.setFullName(req.getName());
        user.setEmail(req.getEmail());
        user.setIsVerified(Boolean.TRUE);
        user.setRole(Role.USER);
        user.setEncryptedPassword(tempPassword);

        userRepository.save(user);

        tempStore.clear(req.getMobileNumber());
    }

//    public LoginResponseDto login(String mobile, String password,String userType) {
//
//        UserEntity user = userRepository.findByMobileNumber(mobile)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!user.getUserType().name().equalsIgnoreCase(userType)) {
//            throw new RuntimeException("Please login from the correct section: " + user.getUserType());
//        }        if (!encoder.matches(password, user.getEncryptedPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        String token = jwt.generateToken(mobile,user.getUserType().name(),user.getRole().name(),user.getId());
//        LoginResponseDto responseDto = LoginResponseDto.builder().token(token)
//                .name(user.getFullName())
//                .userId(user.getId())
//                .userType(user.getUserType())
//                .role(user.getRole().name())
//                .build();
//        return responseDto;
//    }
public LoginResponseDto login(String mobile, String password, String userType) {

    UserEntity user = userRepository.findByMobileNumber(mobile)
            .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            );

    if (!user.getUserType().name().equalsIgnoreCase(userType)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please login from the correct section: " + user.getUserType()
        );
    }

    if (!encoder.matches(password, user.getEncryptedPassword())) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid mobile number or password"
        );
    }

    String token = jwt.generateToken(
            mobile,
            user.getUserType().name(),
            user.getRole().name(),
            user.getId()
    );

    return LoginResponseDto.builder()
            .token(token)
            .name(user.getFullName())
            .userId(user.getId())
            .userType(user.getUserType())
            .role(user.getRole().name())
            .build();
}
}
