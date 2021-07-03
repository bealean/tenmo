package com.techelevator.tenmo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.config.*;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenmoApplication.class)
@TestPropertySource(value = "classpath:application-test.properties")
@AutoConfigureMockMvc

public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private TransfersRepository transfersRepository;

    @Before
    public void setUpTest() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void unauthenticatedRequestsReturnStatusUnauthorized() throws Exception {

        mockMvc.perform(get("/balance")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/transfers")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

        UserDTO fromUser = new UserDTO(1001L, "john");
        UserDTO toUser = new UserDTO(1002L, "jane");

        Transfer transfer = new Transfer(3001L,"Send","Approved",fromUser,toUser, new BigDecimal("50.00"));

        String transferContent = mapper.writeValueAsString(transfer);
        mockMvc.perform(post("/transfers").contentType(MediaType.APPLICATION_JSON).content(transferContent)).andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "jane")
    @Test
    public void getBalanceReturnsStatusOkAndUserBalance() throws Exception {
        UserDTO user = new UserDTO(1001L, "jane");
        userRepository.save(user);
        Accounts account = new Accounts(2001,1001,new BigDecimal("1000.00"));
        accountsRepository.save(account);
        UserDTO otherUser = new UserDTO(1002L, "jamie");
        userRepository.save(otherUser);
        Accounts otherAccount = new Accounts(2002,1002,new BigDecimal("2000.00"));
        accountsRepository.save(otherAccount);

        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1000.00"));
    }

    @WithMockUser(username = "MockUser1")
    @Test
    public void getUsersReturnsStatusOkAndAllUsers() throws Exception {
        UserDTO user1 = new UserDTO(1001L, "john");
        userRepository.save(user1);
        UserDTO user2 = new UserDTO(1002L, "jane");
        userRepository.save(user2);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("john")))
                .andExpect(jsonPath("$[1].id", is(1002)));
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferFromUserReturnsStatusCreatedAndTransfer() throws Exception {
        UserDTO userFrom = new UserDTO(1002L, "jane");
        userRepository.save(userFrom);
        Accounts accountFrom = new Accounts(2002,1002,new BigDecimal("1000.00"));
        accountsRepository.save(accountFrom);
        UserDTO userTo = new UserDTO(1001L, "john");
        userRepository.save(userTo);
        Accounts accountTo = new Accounts(2001,1001,new BigDecimal("1000.00"));
        accountsRepository.save(accountTo);

        Transfer transfer = new Transfer(3001L, "Send", "Approved", userFrom,userTo, new BigDecimal("50.00"));
        String transferContent = mapper.writeValueAsString(transfer);

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("transferId", is(3001)));
    }

    @WithMockUser(username = "john")
    @Test
    public void postTransferNotFromUserReturnsStatusForbidden() throws Exception {
        UserDTO userFrom = new UserDTO(1002L, "jane");
        userRepository.save(userFrom);
        Accounts accountFrom = new Accounts(2002,1002,new BigDecimal("1000.00"));
        accountsRepository.save(accountFrom);
        UserDTO userTo = new UserDTO(1001L, "john");
        userRepository.save(userTo);
        Accounts accountTo = new Accounts(2001,1001,new BigDecimal("1000.00"));
        accountsRepository.save(accountTo);

        Transfer transfer = new Transfer(3001L, "Send", "Approved", userFrom,userTo, new BigDecimal("50.00"));
        String transferContent = mapper.writeValueAsString(transfer);
        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferNegativeAmountReturnsStatusBadRequest() throws Exception {
        UserDTO userFrom = new UserDTO(1002L, "jane");
        UserDTO userTo = new UserDTO(1001L, "john");

        Transfer transfer = new Transfer(3001L, "Send", "Approved", userFrom,userTo, new BigDecimal("-50.00"));
        String transferContent = mapper.writeValueAsString(transfer);
        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferNullTypeReturnsStatusBadRequest() throws Exception {

        String transferContent = "{\"transferId\":3001,\"transferType\":null,\"transferStatus\":\"Approved\",\"userFrom\":{\"id\":1002,\"username\":\"jane\"},\"userTo\":{\"id\":1001,\"username\":\"john\"},\"amount\":50.00}";

            mockMvc.perform(post("/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(transferContent))
                    .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferNullStatusReturnsStatusBadRequest() throws Exception {

        String transferContent = "{\"transferId\":3001,\"transferType\":\"Send\",\"transferStatus\":null,\"userFrom\":{\"id\":1002,\"username\":\"jane\"},\"userTo\":{\"id\":1001,\"username\":\"john\"},\"amount\":50.00}";

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferNullUserFromReturnsStatusBadRequest() throws Exception {
        UserDTO userFrom = null;
        UserDTO userTo = new UserDTO(1001L, "john");

        Transfer transfer = new Transfer(3001L, "Send", "Approved", userFrom,userTo, new BigDecimal("-50.00"));
        String transferContent = mapper.writeValueAsString(transfer);
        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "jane")
    @Test
    public void postTransferNullUserToReturnsStatusBadRequest() throws Exception {
        UserDTO userTo = null;
        UserDTO userFrom = new UserDTO(1001L, "john");

        Transfer transfer = new Transfer(3001L, "Send", "Approved", userFrom,userTo, new BigDecimal("-50.00"));
        String transferContent = mapper.writeValueAsString(transfer);
        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferContent))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "john")
    @Test
    public void getTransfersReturnsStatusOkAndOnlyTransfersFromOrToUser() throws Exception {

        UserDTO user1 = new UserDTO(1001L, "john");
        userRepository.save(user1);
        Accounts account1 = new Accounts(2001,1001,new BigDecimal("1000.00"));
        accountsRepository.save(account1);

        Transfers transferFrom = new Transfers(3001L,2001L,2002L,new BigDecimal("50.00"));
        transfersRepository.save(transferFrom);

        Transfers transferTo = new Transfers(3002L,2002L,2001L,new BigDecimal("50.00"));
        transfersRepository.save(transferTo);

        Transfers transferNotIncludingTestUser = new Transfers(3003L,2002L,2003L,new BigDecimal("50.00"));
        transfersRepository.save(transferNotIncludingTestUser);

        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].transferId", is(3001)))
                .andExpect(jsonPath("$[1].transferId", is(3002)));
    }

}