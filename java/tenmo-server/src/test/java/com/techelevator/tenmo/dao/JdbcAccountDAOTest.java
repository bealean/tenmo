package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.TenmoApplication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenmoApplication.class)
public class JdbcAccountDAOTest {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static boolean isDatabaseConfigured = false;

    private static SingleConnectionDataSource dataSource;
    private static AccountDAO accountDAO;
    private static UserDAO userDAO;
    private static JdbcTemplate jdbcTemplate;

    @Before
    public void configureDatabase() {
        if (!isDatabaseConfigured) {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setAutoCommit(false);
        jdbcTemplate = new JdbcTemplate(dataSource);
        accountDAO = new JdbcAccountDAO(jdbcTemplate);
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
    public void createUser_CheckInitialBalance() {
        userDAO.create("JUnit1", "JUnit1Pwd");
        Long userId = userDAO.findIdByUsername("JUnit1");
        BigDecimal actualBalance = accountDAO.getBalanceGivenUserId(userId);
        Assert.assertEquals(new BigDecimal("1000.00"), actualBalance);
    }

    @Test
    public void getAccountIdFromUserId_UpdateBalanceByAccountId_CheckBalanceByUserId() {
        userDAO.create("JUnit2", "JUnit2Pwd");
        Long userId = userDAO.findIdByUsername("JUnit2");
        BigDecimal userIdOriginalBalance = accountDAO.getBalanceGivenUserId(userId);
        Long accountId = accountDAO.getAccountIdFromUserId(userId);
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql, new BigDecimal("2000"), accountId);
        BigDecimal userIdUpdatedBalance = accountDAO.getBalanceGivenUserId(userId);

        Assert.assertTrue(userIdUpdatedBalance.compareTo(new BigDecimal("2000")) == 0 &&
                userIdUpdatedBalance.compareTo(userIdOriginalBalance) != 0);
    }

    @Test
    public void getBalance_UserIdNotFound() {
        String message = "";
        try {
            accountDAO.getBalanceGivenUserId(0L);
        } catch (RuntimeException e) {
            message = e.getMessage();
            System.out.println(message);
        }
        Assert.assertEquals("Balance not found for User Id 0.",message);
    }

}