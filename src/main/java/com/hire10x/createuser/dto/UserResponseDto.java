package com.hire10x.createuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private long count;
    private int pageCount;
    private int currentPage;
    private List<UserModelDto> users;
}
