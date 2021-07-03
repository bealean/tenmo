package com.techelevator.tenmo.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class TransferTest {

    @Test
    public void transferWithInvalidTypeThrowsException() {
        UserDTO fromUser = new UserDTO();
        UserDTO toUser = new UserDTO();
        String exceptionMessage = "";
        try {
            new Transfer(0L, "Bad Type", "Rejected", fromUser, toUser, new BigDecimal("10"));
        } catch (IllegalArgumentException e) {
            exceptionMessage = e.getMessage();
        }
        Assert.assertTrue(exceptionMessage.contains("not a valid Transfer Type"));
    }

    @Test
    public void transferWithInvalidStatusThrowsException() {
        UserDTO fromUser = new UserDTO();
        UserDTO toUser = new UserDTO();
        String exceptionMessage = "";
        try {
            new Transfer(0L, "Request", "Bad Status", fromUser, toUser, new BigDecimal("10"));
        } catch (IllegalArgumentException e) {
            exceptionMessage = e.getMessage();
        }
        Assert.assertTrue(exceptionMessage.contains("not a valid Transfer Status"));
    }

    @Test
    public void noExceptionForValidTransfer() {
        UserDTO fromUser = new UserDTO();
        UserDTO toUser = new UserDTO();
        boolean isException = false;
        try {
            new Transfer(0L, "Request", "Rejected", fromUser, toUser, new BigDecimal("10"));
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        Assert.assertFalse(isException);
    }

}