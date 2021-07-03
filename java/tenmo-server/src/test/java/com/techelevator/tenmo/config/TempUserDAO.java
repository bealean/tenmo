package com.techelevator.tenmo.config;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("test")
public class TempUserDAO implements UserDAO {

    private UsersRepository userRepository;
    private AccountsRepository accountsRepository;

    public TempUserDAO(UsersRepository userRepository, AccountsRepository accountsRepository) {
        this.userRepository = userRepository;
        this.accountsRepository = accountsRepository;
    }

    @Override
    public List<UserDTO> getUserDTOList() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public Long findIdByUsername(String username) {
        return userRepository.findUserIdFromUsername(username);
    }

    @Override
    public boolean create(String username, String password) {
        return false;
    }
}
