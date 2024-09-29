package com.hire10x.createuser.service;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.dto.UserResponseDto;
import com.hire10x.createuser.exceptions.UserNotFoundException;
import com.hire10x.createuser.model.UserModel;
import com.hire10x.createuser.repository.UserRepo;
import com.hire10x.createuser.mapper.UserModelMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = "com.hire10x.createuser")
class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private UserModelMapper userModelMapper;


    @InjectMocks
    private UserService userService;

    // addUser method tests
    @Test
    void UserService_CreateUser_ReturnsUserId() {
        // Arrange
        UserModel userModel = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .password("Password@123")
                .role("ADMIN")
                .customerId("Company1")
                .phone("1234567890")
                .email("john.doe@example.com")
                .build();


        UserModel savedUser = UserModel.builder()
                .id(1001L) // Simulate sequence-generated ID
                .firstName("firstName")
                .lastName("lastName")
                .userId("firstName1001")
                .password("Password@123")
                .role("ADMIN")
                .customerId("Company1")
                .phone("1234567890")
                .build();

        // Assumption
        when(userRepo.save(any(UserModel.class))).thenReturn(savedUser);

        // Act
        String resultUserId = userService.addUser(userModel);

        // Assert
        Assertions.assertThat(resultUserId).isEqualTo("firstName1001");
    }

    @Test
    void UserService_CreateUser_ThrowsException_OnDuplicateEmailOrPhone() {
        // Arrange
        UserModel userModel = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .password("Password@123")
                .role("ADMIN")
                .customerId("Company1")
                .phone("1234567890")
                .build();

        // Assumption
        when(userRepo.save(any(UserModel.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // Act
        RuntimeException exception = Assertions.catchThrowableOfType(
                () -> userService.addUser(userModel),
                RuntimeException.class
        );

        // Assert
        Assertions.assertThat(exception.getMessage()).isEqualTo("Email or phone number already exists");
    }

    @Test
    void UserService_CreateUser_Success() {
        // Arrange
        UserModel userModel = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .password("Password@123")
                .role("ADMIN")
                .customerId("Company1")
                .email("newuser@example.com")
                .phone("1234567890")
                .build();

        // Assumption
        when(userRepo.save(Mockito.any(UserModel.class))).thenReturn(userModel);

        // Act
        String userId = userService.addUser(userModel);

        // Assert
        Assertions.assertThat(userId).isEqualTo(userModel.getUserId());
    }



    // GetUserById service method tests
    @Test
    void UserService_GetUserByUserId_ReturnsUserModelDto() {
        // Arrange
        String userId = "firstName1001";

        UserModel userModel = UserModel.builder()
                .id(1001L)
                .firstName("firstName")
                .lastName("lastName")
                .userId(userId)
                .password("Password@123")
                .role("ADMIN")
                .customerId("Company1")
                .phone("1234567890")
                .build();

        UserModelDto expectedDto = UserModelDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .userId(userId)
                .role("ADMIN")
                .customerId("Company1")
                .phone("1234567890")
                .middleName("m")
                .designation("designation")
                .email("firstName@example.com")
                .build();

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(userModel));
        when(userModelMapper.toDto(userModel)).thenReturn(expectedDto);

        // Act
        UserModelDto resultDto = userService.getUserByUserid(userId);

        // Assert
        Assertions.assertThat(resultDto).isEqualTo(expectedDto);
        Assertions.assertThat(resultDto.getUserId()).isEqualTo(userId);

    }


    @Test
    void UserService_GetUserByUserId_ThrowsUserNotFoundException() {
        // Arrange
        String userId = "firstName101";
        when(userRepo.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = Assertions.catchThrowableOfType(
                () -> userService.getUserByUserid(userId),
                UserNotFoundException.class
        );

        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo("User not found with ID: " + userId);
    }



    @Test
    void testUpdateStatus_Success() {
        // Arrange
        String userId = "firstName1001";
        String newStatus = "DISABLED";
        UserModel userModel = UserModel.builder()
                .userId(userId)
                .firstName("firstName")
                .status("ENABLED")
                .build();

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(userModel));

        // Act
        String result = userService.updateStatus(userId, newStatus);

        // Assert
        Assertions.assertThat(result).isEqualTo("User status changed successfully to DISABLED");
        Assertions.assertThat(userModel.getStatus()).isEqualTo("DISABLED");
        verify(userRepo, times(1)).save(userModel);  // This is to verify that the userRepo.save method was called
    }

    @Test
    void testUpdateStatus_AlreadySameStatus() {
        // Arrange
        String userId = "firstName1001";
        String status = "ENABLED";
        UserModel userModel = UserModel.builder()
                .userId(userId)
                .firstName("firstName")
                .status(status)
                .build();

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(userModel));

        // Act
        String result = userService.updateStatus(userId, status);

        // Assert
        Assertions.assertThat(result).isEqualTo("User status is already ENABLED");
        verify(userRepo, never()).save(any(UserModel.class));  // This is to Verify that userRepo.save is not called
    }


    @Test
    void testUpdateStatus_UserNotFound() {
        // Arrange
        String userId = "firstName001";
        String status = "DISABLED";

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> userService.updateStatus(userId, status))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with ID: " + userId);

        verify(userRepo, never()).save(any(UserModel.class));  // This is to Verify that userRepo.save is not called
    }



// updateUser method tests
    @Test
    void testUpdateUser_SuccessfulUpdate() {
        // Arrange
        String userId = "firstName1001";
        UserModel existingUser = UserModel.builder()
                .userId(userId)
                .firstName("firstName")
                .lastName("lastName")
                .email("firstname@gmail.com")
                .phone("9876543210")
                .role("ADMIN")
                .customerId("Company1")
                .build();

        UserModel updatedUser = UserModel.builder()
                .firstName("newFirstName")
                .lastName("newLastName")
                .email("newfirstname@gmail.com")
                .phone("1234567890") // Corrected phone number
                .role("ADMIN")
                .customerId("Company2")
                .designation("Manager")
                .build();

        UserModelDto userDto = new UserModelDto();
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(existingUser)).thenReturn(existingUser);
        when(userModelMapper.toDto(existingUser)).thenReturn(userDto);

        // Act
        UserModelDto result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        assertEquals("newFirstName", existingUser.getFirstName());
        assertEquals("newLastName", existingUser.getLastName());
        assertEquals("newfirstname@gmail.com", existingUser.getEmail());
        assertEquals("1234567890", existingUser.getPhone());
        assertEquals("ADMIN", existingUser.getRole());
        assertEquals("Manager", existingUser.getDesignation());
        assertEquals("Company2", existingUser.getCustomerId());
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        String userId = "firstName1001";
        UserModel updatedUser = UserModel.builder().build();

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userId, updatedUser));

        // Assert
        assertEquals("User not found with ID: firstName1001", exception.getMessage());
        verify(userRepo, never()).save(any(UserModel.class));
    }

    @Test
    void testUpdateUser_DataIntegrityViolation() {
        // Arrange
        String userId = "firstName1001";
        UserModel existingUser = UserModel.builder()
                .userId(userId)
                .build();
        UserModel updatedUser = UserModel.builder().build();

        // Assumption
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(existingUser)).thenThrow(new DataIntegrityViolationException(""));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.updateUser(userId, updatedUser));

        // Assert
        assertEquals("Email or phone already Exists", exception.getMessage());
        verify(userRepo, times(1)).save(existingUser);
    }

    // Get all users tests
    @Test
    void testGetAllUsers_FilterByCompanyId() {
        // Arrange
        String companyId = "apple";

        // Creating two UserModel instances for testing
        UserModel user1 = UserModel.builder()
                .userId("fname1001")
                .firstName("fname")
                .lastName("lname")
                .role("ADMIN")
                .status("ENABLED")
                .customerId(companyId)
                .build();

        UserModel user2 = UserModel.builder()
                .userId("fname1002")
                .firstName("fname")
                .lastName("lname")
                .role("ADMIN")
                .status("DISABLED")
                .customerId(companyId)
                .build();

        // Adding the users to a list
        List<UserModel> users = Arrays.asList(user1, user2);

        // Creating corresponding UserModelDto instances using the builder pattern
        List<UserModelDto> userDtos = Arrays.asList(
                UserModelDto.builder()
                        .userId("fname1001")
                        .firstName("fname")
                        .lastName("lname")
                        .role("ADMIN")
                        .customerId(companyId)
                        .build(),
                UserModelDto.builder()
                        .userId("fname1002")
                        .firstName("fname")
                        .lastName("lname")
                        .role("ADMIN")
                        .customerId(companyId)
                        .build()
        );

        // Mocking the repository and mapper calls
        when(userRepo.findAll()).thenReturn(users);
        when(userModelMapper.toDto(anyList())).thenReturn(userDtos);

        // Act
        UserResponseDto response = userService.getAllUsers(null, null, companyId, 0, 10);

        // Assert
        assertEquals(2, response.getCount()); // We expect two users in the response
        verify(userRepo, times(1)).findAll(); // Ensure the repository's findAll method was called once
        verify(userModelMapper, times(1)).toDto(anyList()); // Ensure the mapper's toDto method was called
    }

    @Test
    void testGetAllUsers_ExceptionHandling() {
        // Arrange
        when(userRepo.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers(null, null, null, 0, 10);
        });

        assertTrue(exception.getMessage().contains("An error occurred while fetching users"));
        verify(userRepo, times(1)).findAll();
    }


    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String username = "validUserId";
        UserModel user = UserModel.builder()
                .userId(username)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("Password@123")
                .role("USER")
                .build();

        when(userRepo.findByUserId(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(username);
        Assertions.assertThat(userDetails.getPassword()).isEqualTo("Password@123");

    }


    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "invalidUserId";
        when(userRepo.findByUserId(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }











}
