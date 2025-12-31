package com.prerana.userservice.entity.converter;

import com.prerana.userservice.dto.UserDto;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.mapper.UserEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class UserEntityConverter {

//    @Autowired
    @Autowired
    private UserEntityMapper mapperUtil;
//    private MapperUtil mapperUtil;

    public UserDto convertToDto(UserEntity entity){
        return mapperUtil.toDto(entity);
    }
}
