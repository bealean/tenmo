package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.exception.TransferCreationException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("prod")
public class JdbcTransferDAO implements TransferDAO {

    @Autowired
    AccountDAO accountDAO;

    private JdbcTemplate template;

    public JdbcTransferDAO(JdbcTemplate template) {
        this.template = template;
        this.accountDAO = new JdbcAccountDAO(template);
    }

    @Override
    public Transfer createTransfer(Transfer transfer) throws NotEnoughFundsException, TransferCreationException {

        long senderUserId = transfer.getUserFrom().getId();
        BigDecimal balanceData = accountDAO.getBalanceGivenUserId(senderUserId);
        BigDecimal senderUpdatedBalance = balanceData.subtract(transfer.getAmount());
        if (senderUpdatedBalance.compareTo(new BigDecimal("0")) < 0) {
            throw new NotEnoughFundsException();
        }
        Long senderAccountId = accountDAO.getAccountIdFromUserId(senderUserId);

        long recipientUserId = transfer.getUserTo().getId();
        BigDecimal recipientBalanceData = accountDAO.getBalanceGivenUserId(recipientUserId);
        BigDecimal recipientUpdatedBalance = recipientBalanceData.add(transfer.getAmount());
        Long recipientAccountId = accountDAO.getAccountIdFromUserId(recipientUserId);

        /* Call stored procedure to insert transfer record and update balances of sender and recipient
        in a single transaction. SimpleJdbcCall didn't work for the PostgreSQL stored procedure,
        so sent the call as a SQL statement. */

        // Last argument is INOUT transfer_id, which will be returned as the created transfer_id
        String sql = "call create_transfer(?,?,?,?,?,?,?,0)";

        Long transferId;

        try {
            transferId = template.queryForObject(sql, Long.class, senderUpdatedBalance, senderAccountId, recipientUpdatedBalance, recipientAccountId, transfer.getTransferStatus(), transfer.getTransferType(), transfer.getAmount());
        } catch (DataAccessException e) {
            throw new TransferCreationException();
        }

        transfer.setTransferId(transferId);

        return transfer;
    }

    @Override
    public List<Transfer> list(Long accountId) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, amount, " +
                "afrom.user_id AS user_id_from, " +
                "ufrom.username AS username_from, " +
                "ato.user_id AS user_id_to, " +
                "uto.username AS username_to " +
                "FROM transfers t " +
                "JOIN accounts afrom ON t.account_from = afrom.account_id " +
                "JOIN accounts ato ON t.account_to = ato.account_id " +
                "JOIN users ufrom ON afrom.user_id = ufrom.user_id " +
                "JOIN users uto ON ato.user_id = uto.user_id " +
                "JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE account_to = ? or account_from = ?" +
                "ORDER BY transfer_id";

        SqlRowSet result = template.queryForRowSet(sql, accountId, accountId);

        while (result.next()) {
            UserDTO userFrom = new UserDTO();
            UserDTO userTo = new UserDTO();
            Long transferId = result.getLong("transfer_id");
            String transferType = result.getString("transfer_type_desc");
            String transferStatus = result.getString("transfer_status_desc");
            userFrom.setId(result.getLong("user_id_from"));
            userFrom.setUsername(result.getString("username_from"));
            userTo.setId(result.getLong("user_id_to"));
            userTo.setUsername(result.getString("username_to"));
            BigDecimal amount = result.getBigDecimal("amount");

            Transfer transfer = new Transfer(transferId, transferType, transferStatus, userFrom, userTo, amount);
            transferList.add(transfer);
        }
        return transferList;
    }
}
