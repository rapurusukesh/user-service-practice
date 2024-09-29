package com.hire10x.createuser.controller;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.dto.UserResponseDto;
import com.hire10x.createuser.dto.UserStatusDto;
import com.hire10x.createuser.exceptions.UserNotFoundException;
import com.hire10x.createuser.model.UserModel;
import com.hire10x.createuser.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserModel user){
        try {
            String userId = userService.addUser(user);
            return new ResponseEntity<>(userId, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }


    @PutMapping("/api/v1/users/{user_id}")
    public ResponseEntity<Object> updateUser(@PathVariable("user_id") String userId, @RequestBody UserModel user){

        try {
            UserModelDto updateUserDto = userService.updateUser(userId, user);
            return new ResponseEntity<>(updateUserDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/api/v1/users/{user_id}")
    public ResponseEntity<String> updateUserStatus(@PathVariable("user_id") String userId, @RequestBody UserStatusDto userStatus ){
        String statusMessage = userService.updateStatus(userId, userStatus.getStatus());

        return new ResponseEntity<>(statusMessage, HttpStatus.OK);
    }

    @GetMapping("/api/v1/users/search/{user_id}")
    public ResponseEntity<UserModelDto> fetchUserByUserid(@PathVariable("user_id") String userId){
        UserModelDto user = userService.getUserByUserid(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/api/v1/users/search")
    public ResponseEntity<UserResponseDto> fetchAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        UserResponseDto allUsers = userService.getAllUsers(role, status, companyId, page, size);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);

    }


    @GetMapping("/api/v1/greet")
    public String greet(){
        return "Welcome to training-user-service app!!!";
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
