package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenmoApplication.class)
public class JdbcUserDAOTest {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static boolean isDatabaseConfigured = false;

    private static SingleConnectionDataSource dataSource;
    private static UserDAO userDAO;

    @Before
    public void configureDatabase() {
        if (!isDatabaseConfigured) {
            dataSource = new SingleConnectionDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setAutoCommit(false);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            userDAO = new JdbcUserDAO(jdbcTemplate);
            isDatabaseConfigured = true;
        }
   }

    @AfterClass
    public static void cleanup() {
        dataSource.destroy();
    }

    @After
    public void tearDown() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void checkUserListForNewUsers() {
        List<UserDTO> originalList = userDAO.getUserDTOList();
        userDAO.create("JUnit1", "JUnit1Pwd");
        userDAO.create("JUnit2", "JUnit2Pwd");
        List<UserDTO> updatedList = userDAO.getUserDTOList();
        Assert.assertEquals(originalList.size() + 2, updatedList.size());
        User jUnit1User = userDAO.findByUsername("JUnit1");
        User jUnit2User = userDAO.findByUsername("JUnit2");
        int matchingUserDetails = 0;
        for (UserDTO user : updatedList) {
            if (
                    user.getId().equals(jUnit1User.getId()) &&
                    user.getUsername().equals(jUnit1User.getUsername()) ||
                    user.getId().equals(jUnit2User.getId()) &&
                    user.getUsername().equals(jUnit2User.getUsername())
            ) {
                matchingUserDetails++;
            }
        }
        Assert.assertEquals(2,matchingUserDetails);
    }

    @Test
    public void findByUsernameThrowsUsernameNotFoundException() {
        boolean isUsernameNotFound = false;
        try {
            userDAO.findByUsername("NonExistentUser");
        } catch (UsernameNotFoundException e) {
            isUsernameNotFound = true;
        }
        Assert.assertTrue(isUsernameNotFound);
    }

    @Test
    public void userNotCreatedWithExistingUsername() {
        userDAO.create("NonUniqueName","password1");
        boolean isDuplicateUsernameCreated = userDAO.create("NonUniqueName","password2");
        Assert.assertFalse(isDuplicateUsernameCreated);
    }
}