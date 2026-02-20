package com.prerana.userservice.repository;

import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByMobileNumber(String mobile);
    Optional<UserEntity> findByEmail(String email);
    @Query("""
    SELECT u FROM UserEntity u
    WHERE u.userType IN ('NGO', 'INDIVIDUAL','CSR')
""")
    List<UserEntity> findHelpers();


    @Query("""
    select u
    from UserEntity u
    join UserSubscriptionEntity us on us.user.id = u.id and us.active = true
    join SubscriptionPlanEntity p on p.id = us.plan.id
    where u.userType = :type
    order by p.priority desc
""")
    List<UserEntity> findHelpersByPriority(@Param("type") UserType type);


}
