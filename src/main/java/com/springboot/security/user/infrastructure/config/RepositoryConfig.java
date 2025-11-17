package com.springboot.security.user.infrastructure.config;

import com.springboot.security.user.domain.repository.UserRepository;
import com.springboot.security.user.infrastructure.repository.UserJpaRepository;
import com.springboot.security.user.infrastructure.repository.UserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {


    @Bean
    public UserRepository userRepository(UserJpaRepository userJpaRepository) {
        return new UserRepositoryAdapter(userJpaRepository);
    }
}
