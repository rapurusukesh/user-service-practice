package com.hire10x.createuser.controller;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.dto.UserResponseDto;
import com.hire10x.createuser.dto.UserStatusDto;
import com.hire10x.createuser.model.UserModel;
import com.hire10x.createuser.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.ArgumentMatchers.eq;


@WebMvcTest(UserController.class)
@ComponentScan(basePackages = "com.hire10x.createuser")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createUser_Success() throws Exception {
        // Arrange: Create a valid user model using the builder pattern
        UserModel user = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("firstname@gmail.com")
                .phone("1234567890")
                .password("Password@123")
                .role("ADMIN")
                .customerId("apple")
                .build();

        // Assumption: Mock the userService to return a specific user ID
        when(userService.addUser(any(UserModel.class))).thenReturn("firstName1001");

        // Act & Assert: Perform the POST request and check the response
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)  // Specify JSON request
                        .content(objectMapper.writeValueAsString(user)))  // Convert user object to JSON
                .andExpect(status().isCreated())  // Expect HTTP status 201 Created
                .andExpect(content().string("firstName1001"));  // Expect the returned user ID
    }


    @Test
    void createUser_ExceptionHandling() throws Exception {
        // Arrange
        UserModel user = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("firstname@gmail.com")
                .phone("1234567890")
                .password("Password@123")
                .role("ADMIN")
                .customerId("apple")
                .build();

        // Mocking service method to throw a RuntimeException
        when(userService.addUser(any(UserModel.class)))
                .thenThrow(new RuntimeException("Error creating user"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error creating user"));
    }


    @Test
    void createUser_BadRequest() throws Exception {
        // Arrange
        UserModel invalidUser = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("invalidEmail")
                .phone("123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    // updateUser tests

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        // Arrange
        String userId = "fname1001";
        UserModel updatedUser = UserModel.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("firstname@gmail.com")
                .phone("1234567890")
                .password("Password@123")
                .role("ADMIN")
                .customerId("apple")
                .build();

        UserModelDto updatedUserDto = UserModelDto.builder()
                .userId(userId)
                .firstName("firstName")
                .lastName("lastName")
                .role("ADMIN")
                .customerId("apple")
                .build();


        when(userService.updateUser(eq(userId), any(UserModel.class))).thenReturn(updatedUserDto);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{user_id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedUserDto)));
    }

    @Test
    void testUpdateUserStatus_Success() {
        // Arrange
        String userId = "firstName1001";
        UserStatusDto userStatusDto = new UserStatusDto();
        userStatusDto.setStatus("ACTIVE");

        when(userService.updateStatus(userId, "ACTIVE")).thenReturn("User status updated successfully");

        // Act
        ResponseEntity<String> response = userController.updateUserStatus(userId, userStatusDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User status updated successfully", response.getBody());
    }


    @Test
    void testFetchAllUsers_Success() {
        // Arrange
        UserResponseDto mockResponse = new UserResponseDto();
        // Add mock data to the UserResponseDto object if needed

        when(userService.getAllUsers(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<UserResponseDto> response = userController.fetchAllUsers("ADMIN", "ACTIVE", "123", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }


    @Test
    void testFetchUserByUserid_Success() {
        // Arrange
        UserModelDto mockUser = new UserModelDto();
        mockUser.setUserId("firstName1001");
        mockUser.setFirstName("firstName");
        mockUser.setLastName("lastName");
        mockUser.setRole("ADMIN");
        mockUser.setCustomerId("company1");

        // Assumption
        when(userService.getUserByUserid(anyString())).thenReturn(mockUser);

        // Act
        ResponseEntity<UserModelDto> response = userController.fetchUserByUserid("12345");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void testFetchUserByUserid_UserNotFound() {
        // Arrange
        when(userService.getUserByUserid(anyString())).thenReturn(null);

        // Act
        ResponseEntity<UserModelDto> response = userController.fetchUserByUserid("invalid-id");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testUpdateUser_CatchBlock_RuntimeException() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUserId("firstName1001");
        userModel.setFirstName("firstName");
        userModel.setLastName("lastName");
        userModel.setRole("ADMIN");
        userModel.setCustomerId("company1");

        String exceptionMessage = "User update failed";

        // Assume a RuntimeException being thrown from the service layer
        doThrow(new RuntimeException(exceptionMessage)).when(userService).updateUser(anyString(), any(UserModel.class));

        // Act
        ResponseEntity<Object> response = userController.updateUser("12345", userModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(exceptionMessage, response.getBody());
    }

}
