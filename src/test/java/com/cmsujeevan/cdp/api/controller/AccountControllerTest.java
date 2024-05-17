package com.cmsujeevan.cdp.api.controller;

import com.cmsujeevan.cdp.api.model.dto.AccountDto;
import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360;
import com.cmsujeevan.cdp.api.service.ContactAccount360Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @Mock
    private ContactAccount360Service contactAccount360Service;

    @InjectMocks
    private AccountController accountController;

    @Test
    public void testCreateContactAccount360() {
        // Create a sample ContactAccount360 object
        ContactAccount360 contactAccount360 = new ContactAccount360();
        contactAccount360.setAmpId("123");

        // Mock the behavior of the service layer
        when(contactAccount360Service.saveContactAccount360(any(ContactAccount360.class))).thenReturn(contactAccount360);

        // Call the controller method
        ResponseEntity<ContactAccount360> response = accountController.createContactAccount360(contactAccount360);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(contactAccount360, response.getBody());
    }

    @Test
    public void testUpdateContactAccount360() {
        // Create a sample ContactAccount360 object
        ContactAccount360 contactAccount360 = new ContactAccount360();
        contactAccount360.setAmpId("123");

        // Mock the behavior of the service layer
        when(contactAccount360Service.updateContactAccount360(any(ContactAccount360.class))).thenReturn(new ResponseEntity(HttpStatus.OK));

        // Call the controller method
        ResponseEntity response = accountController.updateContactAccount360(contactAccount360);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetContactAccountByAmpId() {
        // Mock the behavior of the service layer
        AccountDto accountDto = new AccountDto();
        when(contactAccount360Service.getContactAccountByAmpId(anyString())).thenReturn(accountDto);

        // Call the controller method
        ResponseEntity<AccountDto> response = accountController.getContactAccountByAmpId("123");

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDto, response.getBody());
    }

    @Test
    public void testGetContactAccountBySyscoCustomerId() {
        // Mock the behavior of the service layer
        AccountDto accountDto = new AccountDto();
        when(contactAccount360Service.getContactAccountBySyscoCustomerId(anyString())).thenReturn(accountDto);

        // Call the controller method
        ResponseEntity<AccountDto> response = accountController.getContactAccountBySyscoCustomerId("123");

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDto, response.getBody());
    }

    @Test
    public void testGetContactAccountByContactSfId() {
        // Mock the behavior of the service layer
        AccountDto accountDto = new AccountDto();
        when(contactAccount360Service.getContactAccountByContactSfId(anyString())).thenReturn(accountDto);

        // Call the controller method
        ResponseEntity<AccountDto> response = accountController.getContactAccountByContactSfId("123");

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDto, response.getBody());
    }

    @Test
    public void testGetContactAccountByAccountSfId() {
        // Mock the behavior of the service layer
        AccountDto accountDto = new AccountDto();
        when(contactAccount360Service.getContactAccountByAccountSfId(anyString())).thenReturn(accountDto);

        // Call the controller method
        ResponseEntity<AccountDto> response = accountController.getContactAccountByAccountSfId("123");

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDto, response.getBody());
    }
}
