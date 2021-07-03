package com.techelevator.tenmo.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("prod")
public class JdbcAccountDAO implements AccountDAO {

    private JdbcTemplate template;

    public JdbcAccountDAO(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public BigDecimal getBalanceGivenUserId(Long id) {
        String sql = "SELECT balance from accounts where user_id = ?";
        try {
            String balance = template.queryForObject(sql, String.class, id);
            /* queryForObject exception if value not returned,
            balance should not be null */
            return new BigDecimal(balance);
        } catch (DataAccessException e) {
            throw new RuntimeException("Balance not found for User Id "+id+".");
        }
    }

    @Override
    public Long getAccountIdFromUserId(Long id) {
        String sql = "SELECT account_id from accounts where user_id =? ";
        return template.queryForObject(sql, Long.class, id);
    }

}
