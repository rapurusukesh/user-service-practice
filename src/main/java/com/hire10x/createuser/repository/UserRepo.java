package com.hire10x.createuser.repository;

import com.hire10x.createuser.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserModel, String> {

    Optional<UserModel> findByUserId(String userId);

}
