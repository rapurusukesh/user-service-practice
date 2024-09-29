package com.hire10x.createuser.mapper;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.model.UserModel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-28T22:11:19-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
@Component
public class UserModelMapperImpl implements UserModelMapper {

    @Override
    public UserModelDto toDto(UserModel userModel) {
        if ( userModel == null ) {
            return null;
        }

        UserModelDto.UserModelDtoBuilder userModelDto = UserModelDto.builder();

        userModelDto.userId( userModel.getUserId() );
        userModelDto.firstName( userModel.getFirstName() );
        userModelDto.lastName( userModel.getLastName() );
        userModelDto.email( userModel.getEmail() );
        userModelDto.phone( userModel.getPhone() );
        userModelDto.middleName( userModel.getMiddleName() );
        userModelDto.designation( userModel.getDesignation() );
        userModelDto.role( userModel.getRole() );
        userModelDto.customerId( userModel.getCustomerId() );
        userModelDto.createdAt( userModel.getCreatedAt() );
        userModelDto.updatedAt( userModel.getUpdatedAt() );

        return userModelDto.build();
    }

    @Override
    public List<UserModelDto> toDto(List<UserModel> userModelList) {
        if ( userModelList == null ) {
            return null;
        }

        List<UserModelDto> list = new ArrayList<UserModelDto>( userModelList.size() );
        for ( UserModel userModel : userModelList ) {
            list.add( toDto( userModel ) );
        }

        return list;
    }
}
