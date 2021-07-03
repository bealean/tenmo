package com.techelevator.tenmo.config;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class TempTransferDAO implements TransferDAO {

    private TransfersRepository transfersRepository;
    private UsersRepository usersRepository;
    private AccountsRepository accountsRepository;

    public TempTransferDAO(TransfersRepository transfersRepository, UsersRepository userRepository, AccountsRepository accountsRepository) {
        this.transfersRepository = transfersRepository;
        this.usersRepository = userRepository;
        this.accountsRepository = accountsRepository;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) throws NotEnoughFundsException {
        UserDAO userDAO = new TempUserDAO(usersRepository, accountsRepository);
        AccountDAO accountDAO = new TempAccountDAO(accountsRepository);
        Long fromUserId = userDAO.findIdByUsername(transfer.getUserFrom().getUsername());
        Long accountFrom = accountDAO.getAccountIdFromUserId(fromUserId);
        Long toUserId = userDAO.findIdByUsername(transfer.getUserTo().getUsername());
        Long accountTo = accountDAO.getAccountIdFromUserId(toUserId);
        Transfers tempTransfer = new Transfers(transfer.getTransferId(),accountFrom,accountTo,transfer.getAmount());
        transfersRepository.save(tempTransfer);
        return transfer;
    }

    @Override
    public List<Transfer> list(Long accountId) {
        List<Transfers> tempTransfers = transfersRepository.findTransfersForAccount(accountId);
        List<Transfer> transfers = new ArrayList<>();
        for (Transfers tempTransfer : tempTransfers) {
            Transfer transfer = new Transfer(tempTransfer.getTransferid(),"Send","Approved",null,null,tempTransfer.getAmount());
            transfers.add(transfer);
        }
        return transfers;
    }
}
