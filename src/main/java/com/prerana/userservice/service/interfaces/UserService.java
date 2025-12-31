package com.prerana.userservice.service.interfaces;

import com.prerana.userservice.dto.UserResponseDto;

public interface UserService {
    UserResponseDto getUserDetails(Long userId);
}
