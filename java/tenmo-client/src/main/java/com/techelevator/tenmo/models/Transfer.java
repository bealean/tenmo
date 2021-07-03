package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {
    // transferId used in deserialization
    private Long transferId;
    private String transferType;
    private String transferStatus;
    private User userFrom;
    private User userTo;
    private BigDecimal amount;

    // Default constructor used in deserialization
    public Transfer() {
    }

    public Transfer(String transferType, String transferStatus, User userFrom, User userTo, BigDecimal transferAmount) {
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.amount = transferAmount;
    }

    public Long getTransferId() { return transferId; }

    public String getTransferType() {
        return transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferType=" + transferType +
                ", transferStatus=" + transferStatus +
                ", userFrom=" + userFrom +
                ", userTo=" + userTo +
                ", amount=" + amount +
                '}';
    }
}