package com.springboot.security.user.infrastructure.repository;

import com.springboot.security.user.domain.entity.User;
import com.springboot.security.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User getByUid(String uid) {
        return userJpaRepository.getByUid(uid);
    }
}
