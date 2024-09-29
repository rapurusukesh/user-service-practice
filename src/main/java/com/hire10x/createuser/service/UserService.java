package com.hire10x.createuser.service;

import com.hire10x.createuser.dto.UserModelDto;
import com.hire10x.createuser.exceptions.UserNotFoundException;

import com.hire10x.createuser.dto.UserResponseDto;
import com.hire10x.createuser.exceptions.DuplicateEntryException;
import com.hire10x.createuser.exceptions.UserServiceException;
import com.hire10x.createuser.mapper.UserModelMapper;
import com.hire10x.createuser.model.UserLoginModel;
import com.hire10x.createuser.model.UserModel;
import com.hire10x.createuser.model.UserPrinciple;
import com.hire10x.createuser.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {
    private static final String USERNOTFOUNDEXCEPTION = "User not found with ID: ";
    private final UserRepo userRepo;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserModelMapper userModelMapper;

    @Autowired
    @Lazy // To avoid circular references
    public UserService(UserRepo userRepo, JWTService jwtService, @Lazy AuthenticationManager authenticationManager, UserModelMapper userModelMapper) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userModelMapper = userModelMapper;
    }


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    
    public String addUser(UserModel user){
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            UserModel savedUser =  userRepo.save(user);
            return savedUser.getUserId();
        } catch (DataIntegrityViolationException e) {
                throw new DuplicateEntryException("Email or phone number already exists");
        }


    }


    public UserModelDto updateUser(String userId, UserModel user){
        Optional<UserModel> existingUserOpt = userRepo.findByUserId(userId);

        if (existingUserOpt.isPresent()) {
            UserModel existingUser = existingUserOpt.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setMiddleName(user.getMiddleName());
            existingUser.setCustomerId(user.getCustomerId());
            if("ADMIN".equals(existingUser.getRole())) {
                existingUser.setDesignation(user.getDesignation());
                existingUser.setRole(user.getRole());
            }


            try {
                userRepo.save(existingUser);
                return userModelMapper.toDto(existingUser);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEntryException("Email or phone already Exists");
            }

        } else {
            throw new UserNotFoundException(USERNOTFOUNDEXCEPTION + userId);
        }



    }


    public String updateStatus(String userId,String status){
        Optional<UserModel> existingUserOpt = userRepo.findByUserId(userId);
        if (existingUserOpt.isPresent()) {
            UserModel existingUser = existingUserOpt.get();
            if(!existingUser.getStatus().equals(status)){
                existingUser.setStatus(status);
            }
            else {
                return "User status is already " + status;
            }

            userRepo.save(existingUser);

        } else {
            throw new UserNotFoundException(USERNOTFOUNDEXCEPTION + userId);
        }


        return "User status changed successfully to " +status;
    }



    public UserResponseDto getAllUsers(String role, String status, String companyId, int page, int size) {
        try {
            List<UserModel> allUsers = userRepo.findAll();

            // Apply filters using streams
            Stream<UserModel> userStream = allUsers.stream();

            if (companyId != null && !companyId.isEmpty()) {
                userStream = userStream.filter(user -> companyId.equals(user.getCustomerId()));
            }

            if (role != null && !role.isEmpty()) {
                userStream = userStream.filter(user -> role.equals(user.getRole()));
            }

            if (status != null && !status.isEmpty()) {
                userStream = userStream.filter(user -> status.equals(user.getStatus()));
            }

            List<UserModel> filteredUsers = userStream.toList();

            // Pagination
            long totalCount = filteredUsers.size();
            int totalPages = (int) Math.ceil((double) totalCount / size);
            int currentPage = Math.max(0, Math.min(page, totalPages - 1)); // Ensure currentPage is valid

            // Perform pagination on the filtered list
            List<UserModel> paginatedUsers = filteredUsers.stream()
                    .skip((long) currentPage * size) // Skip records of previous pages
                    .limit(size) // Limit to the requested page size
                    .toList();

            // Convert to DTOs
            List<UserModelDto> userDtos = userModelMapper.toDto(paginatedUsers);

            // Return response DTO
            return new UserResponseDto(totalCount, totalPages, currentPage, userDtos);
        } catch (Exception e) {
            throw new UserServiceException("An error occurred while fetching users: " + e.getMessage(), e);
        }
    }



    public UserModelDto getUserByUserid(String userId) {
        Optional<UserModel> userOpt = userRepo.findByUserId(userId);

        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            return userModelMapper.toDto(user);
        } else {
            throw new UserNotFoundException(USERNOTFOUNDEXCEPTION + userId);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserModel> optionalUser = userRepo.findByUserId(username);
        UserModel user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }


        return new UserPrinciple(user);
    }

    public String verify(UserLoginModel userLoginModel) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginModel.getUserId(),userLoginModel.getPassword()));


        if(authentication.isAuthenticated()){
            return  jwtService.generateToken(userLoginModel.getUserId());

        }

        return "failed";
    }
}








