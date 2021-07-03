package com.techelevator.tenmo.config;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Transfers {
    @Id
    private long transferid;
    private long accountfrom;
    private long accountto;
    private BigDecimal amount;

    public Transfers() {
    }

    public Transfers(long transferid, long accountfrom, long accountto, BigDecimal amount) {
        this.transferid = transferid;
        this.accountfrom = accountfrom;
        this.accountto = accountto;
        this.amount = amount;
    }

    public long getTransferid() {
        return transferid;
    }

    public void setTransferid(long transferid) {
        this.transferid = transferid;
    }

    public long getAccountfrom() {
        return accountfrom;
    }

    public void setAccountfrom(long accountfrom) {
        this.accountfrom = accountfrom;
    }

    public long getAccountto() {
        return accountto;
    }

    public void setAccountto(long accountto) {
        this.accountto = accountto;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
