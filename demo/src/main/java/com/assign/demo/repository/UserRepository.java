package com.assign.demo.repository;

import com.assign.demo.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetails, Long> {
    
    // Add a native query method to directly query the database
    @Query(value = "SELECT * FROM user_details WHERE id = :id", nativeQuery = true)
    Optional<UserDetails> findUserByIdNative(@Param("id") Long id);
}