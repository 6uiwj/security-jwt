package com.springboot.security.user.domain.repository;

import com.springboot.security.user.domain.entity.User;

public interface UserRepository {
    User getByUid(String uid);

    User save(User user);
}
