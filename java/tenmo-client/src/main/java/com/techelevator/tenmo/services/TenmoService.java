package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {

    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    public TenmoService(String url) {
        this.BASE_URL = url;
    }

    public BigDecimal getBalanceData(String authToken) throws TenmoServiceException {
        try {
            return restTemplate.exchange(BASE_URL + "/balance", HttpMethod.GET, makeAuthEntity(authToken), BigDecimal.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new TenmoServiceException(ex.getMessage());
        }
    }

    public User[] listUsers(String authToken) throws TenmoServiceException {
        try {
            return restTemplate.exchange(BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(authToken), User[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new TenmoServiceException(ex.getMessage());
        }
    }

    public void createTransfer(String authToken, Transfer transfer) throws TenmoServiceException {
        try {
            restTemplate.postForObject(BASE_URL + "/transfers", makeTransferEntity(authToken, transfer), Transfer.class);
        } catch (RestClientResponseException ex) {
            throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new TenmoServiceException(ex.getMessage());
        }
    }

    public Transfer[] listTransfers(String authToken) throws TenmoServiceException {
        try {
            return restTemplate.exchange(BASE_URL + "/transfers", HttpMethod.GET, makeAuthEntity(authToken), Transfer[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new TenmoServiceException(ex.getMessage());
        }
    }

    /**
     * Creates a new {HttpEntity} with the `Authorization: Bearer:` header and a transfer request body
     *
     * @param transfer Transfer to  include in request body.
     * @return HttpEntity<Transfer>
     */
    private HttpEntity<Transfer> makeTransferEntity(String authToken, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    /**
     * Returns an {HttpEntity} with the `Authorization: Bearer:` header
     *
     * @return {HttpEntity}
     */
    private HttpEntity makeAuthEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
