package com.springboot.security.user.infrastructure.repository;

import com.springboot.security.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
   User getByUid(String uid);

}
