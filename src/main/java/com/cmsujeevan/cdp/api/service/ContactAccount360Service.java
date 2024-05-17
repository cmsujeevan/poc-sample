package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.model.dto.AccountDto;
import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360;
import org.springframework.http.ResponseEntity;

public interface ContactAccount360Service {

    void insertRecordsFromTempTable();

    ContactAccount360 saveContactAccount360(ContactAccount360 contactAccount360);

    ResponseEntity updateContactAccount360(ContactAccount360 contactAccount360);

    AccountDto getContactAccountByAmpId(String amp_id);

    AccountDto getContactAccountBySyscoCustomerId(String sys_cust_id);

    AccountDto getContactAccountByContactSfId(String contact_sf_id);

    AccountDto getContactAccountByAccountSfId(String account_sf_id);
}
