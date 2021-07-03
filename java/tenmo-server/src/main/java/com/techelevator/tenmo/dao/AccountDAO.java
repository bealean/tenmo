package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDAO {

    BigDecimal getBalanceGivenUserId(Long id);
    Long getAccountIdFromUserId(Long id);

}
