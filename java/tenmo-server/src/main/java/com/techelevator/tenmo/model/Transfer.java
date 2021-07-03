package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Transfer {
    private Long transferId;
    @NotNull
    private String transferType;
    @NotNull
    private String transferStatus;
    @NotNull
    private UserDTO userFrom;
    @NotNull
    private UserDTO userTo;
    @Positive(message = "Transfer amount cannot be negative.")
    private BigDecimal amount;

    private final List<String> TRANSFER_TYPES = Arrays.asList("Send", "Request");
    private final List<String> TRANSFER_STATUSES = Arrays.asList("Approved", "Pending", "Rejected");

    public Transfer(Long transferId, String transferType, String transferStatus, UserDTO userFrom, UserDTO userTo, BigDecimal amount) {
        if (!TRANSFER_TYPES.contains(transferType)) {
            throw new IllegalArgumentException(transferType + " is not a valid Transfer Type. Valid types: " + TRANSFER_TYPES);
        }
        if (!TRANSFER_STATUSES.contains(transferStatus)) {
            throw new IllegalArgumentException(transferStatus + " is not a valid Transfer Status. Valid statuses: " + TRANSFER_STATUSES);
        }
        this.transferId = transferId;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.amount = amount;
    }
    public Transfer(){}

    // Used in serialization
    public Long getTransferId() {
        return transferId;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public void setUserFrom(UserDTO userFrom) {
        this.userFrom = userFrom;
    }

    public void setUserTo(UserDTO userTo) {
        this.userTo = userTo;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public UserDTO getUserFrom() {
        return userFrom;
    }

    public UserDTO getUserTo() {
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(transferId, transfer.transferId) &&
                Objects.equals(transferType, transfer.transferType) &&
                Objects.equals(transferStatus, transfer.transferStatus) &&
                Objects.equals(userFrom, transfer.userFrom) &&
                Objects.equals(userTo, transfer.userTo) &&
                Objects.equals(amount, transfer.amount);
    }
}
