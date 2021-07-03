package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.exception.NotAuthorizedException;
import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.exception.TransferCreationException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class ApplicationController {

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    TransferDAO transferDAO;

    @ApiOperation("Returns the balance of the currently authenticated user")
    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(@ApiIgnore Principal principal) {
        Long correspondingUserId = userDAO.findIdByUsername(principal.getName());
        return accountDAO.getBalanceGivenUserId(correspondingUserId);
    }

    @ApiOperation("Returns a list of all of the users")
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<UserDTO> getUserList() {
        return userDAO.getUserDTOList();
    }

    @ApiOperation("Transfers money from the currently authenticated user to the transfer recipient")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody @ApiParam("Transfer details") Transfer transfer, @ApiIgnore Principal principal) throws NotEnoughFundsException, NotAuthorizedException, TransferCreationException {
        if (principal.getName().equals(transfer.getUserFrom().getUsername())) {
            return transferDAO.createTransfer(transfer);
        } else {
            throw new NotAuthorizedException();
        }
    }

    @ApiOperation("Returns list of all transfers sent and received by the currently authenticated user")
    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransferList(@ApiIgnore Principal principal) {
        Long correspondingUserId = userDAO.findIdByUsername(principal.getName());
        Long accountId = accountDAO.getAccountIdFromUserId(correspondingUserId);
        return transferDAO.list(accountId);
    }

}
