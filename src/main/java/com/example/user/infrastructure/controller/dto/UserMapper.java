package com.example.user.infrastructure.controller.dto;

import com.example.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User mapToEntity(UserRequestDto dto);

    UserResponseDto mapToEntity(User user);
}
