package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;

import java.util.List;

public interface UserDAO {

    List<UserDTO> getUserDTOList();

    User findByUsername(String username);

    Long findIdByUsername(String username);

    boolean create(String username, String password);

}
