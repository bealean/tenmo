package com.techelevator.tenmo.config;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Accounts {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private long accountid;

    private long userid;
    private BigDecimal balance;

    public Accounts() {
    }

    public Accounts(long accountid, long userid, BigDecimal balance) {
        this.accountid = accountid;
        this.userid = userid;
        this.balance = balance;
    }

    public long getAccountid() {
        return accountid;
    }

    public void setAccountid(long accountid) {
        this.accountid = accountid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
