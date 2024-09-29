package com.hire10x.createuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModelDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String middleName;
    private String designation;
    private String role;
    private String customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
