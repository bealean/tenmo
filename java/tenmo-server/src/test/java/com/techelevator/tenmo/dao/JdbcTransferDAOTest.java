package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.exception.TransferCreationException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserDTO;

import org.junit.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenmoApplication.class)
public class JdbcTransferDAOTest {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static boolean isDatabaseConfigured = false;

    private static SingleConnectionDataSource dataSource;
    private static TransferDAO transferDAO;
    private static AccountDAO accountDAO;
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
            transferDAO = new JdbcTransferDAO(jdbcTemplate);
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
    public void createValidTransfer() throws NotEnoughFundsException, TransferCreationException {

        UserDTO userFrom = createUserDTO("UserFrom");
        UserDTO userTo = createUserDTO("UserTo");

        BigDecimal beforeTestBalanceData = accountDAO.getBalanceGivenUserId(userFrom.getId());
        BigDecimal beforeTestBalanceData2 = accountDAO.getBalanceGivenUserId(userTo.getId());

        Transfer testTransfer = createTestTransfer(userFrom, userTo, new BigDecimal("25.00"));
        Transfer createdTransfer = transferDAO.createTransfer(testTransfer);

        Assert.assertNotNull(createdTransfer);

        BigDecimal testBalanceData = accountDAO.getBalanceGivenUserId(userFrom.getId());
        BigDecimal testBalanceData2 = accountDAO.getBalanceGivenUserId(userTo.getId());

        BigDecimal expectedBalanceData = beforeTestBalanceData.subtract(new BigDecimal("25.00"));
        BigDecimal expectedBalanceData2 = beforeTestBalanceData2.add(new BigDecimal("25.00"));

        boolean isBalanceCorrect = false;

        if (expectedBalanceData.compareTo(testBalanceData) == 0 && expectedBalanceData2.compareTo(testBalanceData2) == 0) {
            isBalanceCorrect = true;
        }
        Assert.assertTrue(isBalanceCorrect);
    }

    @Test
    public void createTransferWithoutEnoughFunds() throws TransferCreationException {
        UserDTO userFrom = createUserDTO("UserFrom");
        UserDTO userTo = createUserDTO("UserTo");

        BigDecimal beforeTestBalanceData = accountDAO.getBalanceGivenUserId(userFrom.getId());
        BigDecimal beforeTestBalanceData2 = accountDAO.getBalanceGivenUserId(userTo.getId());

        Transfer testTransfer = createTestTransfer(userFrom, userTo, beforeTestBalanceData.add(new BigDecimal("1.00")));

        boolean notEnoughFunds = false;

        try {
            transferDAO.createTransfer(testTransfer);
        } catch (NotEnoughFundsException e) {
            notEnoughFunds = true;
        }
        BigDecimal afterTestBalanceData = accountDAO.getBalanceGivenUserId(userFrom.getId());
        BigDecimal afterTestBalanceData2 = accountDAO.getBalanceGivenUserId(userTo.getId());

        Assert.assertTrue(notEnoughFunds);
        Assert.assertEquals(beforeTestBalanceData, afterTestBalanceData);
        Assert.assertEquals(beforeTestBalanceData2, afterTestBalanceData2);
    }

    @Test
    public void invalid_transfer_for_insert() throws NotEnoughFundsException {
        UserDTO userFrom = createUserDTO("UserFrom");
        UserDTO userTo = createUserDTO("UserTo");

        Transfer testTransfer = createTestTransfer(userFrom, userTo, new BigDecimal(".001"));
        boolean transferCreationException = false;
        try {
            transferDAO.createTransfer(testTransfer);
        } catch (TransferCreationException e) {
            transferCreationException = true;
        }
        Assert.assertTrue(transferCreationException);
    }

    @Test
    public void list_only_includes_transfers_to_or_from_user() throws NotEnoughFundsException, TransferCreationException {
        UserDTO user1 = createUserDTO("User1");
        UserDTO user2 = createUserDTO("User2");
        UserDTO user3 = createUserDTO("User3");

        Transfer testTransfer1 = createTestTransfer(user1, user2, new BigDecimal("50.00"));
        transferDAO.createTransfer(testTransfer1);
        Transfer testTransfer2 = createTestTransfer(user2, user1, new BigDecimal("25.00"));
        transferDAO.createTransfer(testTransfer2);
        Transfer testTransfer3 = createTestTransfer(user2, user3, new BigDecimal("10.00"));
        transferDAO.createTransfer(testTransfer3);

        Long user1AccountId = accountDAO.getAccountIdFromUserId(user1.getId());
        Long user3AccountId = accountDAO.getAccountIdFromUserId(user3.getId());

        List<Transfer> actualList = transferDAO.list(user1AccountId);
        Assert.assertEquals(2, actualList.size());
        Assert.assertEquals(testTransfer1, actualList.get(0));
        Assert.assertEquals(testTransfer2, actualList.get(1));

        actualList = transferDAO.list(user3AccountId);
        Assert.assertEquals(1, actualList.size());
        Assert.assertEquals(testTransfer3, actualList.get(0));

    }

    private UserDTO createUserDTO(String name) {
        userDAO.create(name, name + "Pwd");
        Long id = userDAO.findIdByUsername(name);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername(name);
        return userDTO;
    }

    private Transfer createTestTransfer(UserDTO from, UserDTO to, BigDecimal amount) {
        Transfer testTransfer = new Transfer();
        testTransfer.setTransferType("Send");
        testTransfer.setTransferStatus("Approved");
        testTransfer.setUserFrom(from);
        testTransfer.setUserTo(to);
        testTransfer.setAmount(amount);
        return testTransfer;
    }

}