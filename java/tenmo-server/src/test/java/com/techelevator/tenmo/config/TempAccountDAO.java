package com.techelevator.tenmo.config;

import com.techelevator.tenmo.dao.AccountDAO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("test")
public class TempAccountDAO implements AccountDAO {

    private AccountsRepository accountsRepository;

    public TempAccountDAO(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    public BigDecimal getBalanceGivenUserId(Long id) {
        return accountsRepository.findBalanceFromUserId(id);
    }

    @Override
    public Long getAccountIdFromUserId(Long id) {
        return accountsRepository.findAccountIdFromUserId(id);
    }

}
