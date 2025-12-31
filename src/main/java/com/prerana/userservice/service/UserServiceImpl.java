package com.prerana.userservice.service;

import com.prerana.userservice.dto.UserResponseDto;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.repository.UserRepository;
import com.prerana.userservice.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponseDto getUserDetails(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getFullName());
        dto.setMobile(user.getMobileNumber());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setUserType(user.getUserType().name());
        dto.setPinCode(user.getPincode());
        dto.setState(user.getState());
        dto.setCity(user.getCity());
        dto.setProfession(user.getProfession());
        dto.setAddress(user.getAddressLine());

        return dto;
    }

    @Transactional
    public void completeIndividualProfile(Long userId, Map<String, String> req) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // safety check

        System.out.println("address is "+req.get("address"));
        user.setAddressLine(req.get("address"));
        user.setCity(req.get("city"));
        user.setState(req.get("state"));
        user.setPincode(req.get("pincode"));
        user.setProfession(req.get("profession"));

        user.setProfileCompleted(true);

        userRepository.save(user);
    }

}
