package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.exception.TransferCreationException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {
    Transfer createTransfer(Transfer transfer) throws NotEnoughFundsException, TransferCreationException;
    List<Transfer> list(Long accountId);
}
