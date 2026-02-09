package com.prerana.userservice.repository;

import com.prerana.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByMobileNumber(String mobile);
    Optional<UserEntity> findByEmail(String email);
    @Query("""
    SELECT u FROM UserEntity u
    WHERE u.userType IN ('NGO', 'INDIVIDUAL')
""")
    List<UserEntity> findHelpers();


}
