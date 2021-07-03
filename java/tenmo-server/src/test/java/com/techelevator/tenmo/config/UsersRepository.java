package com.techelevator.tenmo.config;

import com.techelevator.tenmo.model.UserDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public interface UsersRepository extends JpaRepository<UserDTO, Long> {
    @Query("SELECT u.id FROM UserDTO u where u.username = :username")
    Long findUserIdFromUsername(@Param("username") String username);
}
