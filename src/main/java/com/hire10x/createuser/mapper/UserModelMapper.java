package com.hire10x.createuser.mapper;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserModelMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "designation", target = "designation")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserModelDto toDto(UserModel userModel);

    List<UserModelDto> toDto(List<UserModel> userModelList);

}
