package com.techelevator.tenmo.config;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("test")
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    @Query("SELECT a.accountid FROM Accounts a where a.userid = :userid")
    Long findAccountIdFromUserId(@Param("userid") Long userid);

    @Query("SELECT a.balance FROM Accounts a where a.userid = :userid")
    BigDecimal findBalanceFromUserId(@Param("userid") Long userid);
}
