package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360;
import com.cmsujeevan.cdp.api.dao.repository.ContactAccount360Repository;
import com.cmsujeevan.cdp.api.service.ContactAccount360Service;
import com.cmsujeevan.cdp.api.model.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
@Slf4j
@RequiredArgsConstructor
public class ContactAccount360ServiceImpl implements ContactAccount360Service {

    private final ContactAccount360Repository contactAccount360Repository;


    @Override
    public void insertRecordsFromTempTable() {
        contactAccount360Repository.insertRecordsFromTempTable();
    }

    @Override
    public ContactAccount360 saveContactAccount360(ContactAccount360 contactAccount360) {
        contactAccount360.setId(null);
        return contactAccount360Repository.save(contactAccount360);
    }

    @Override
    public ResponseEntity updateContactAccount360(ContactAccount360 contactAccount360) {
        Optional<ContactAccount360> optionalContactAccount = contactAccount360Repository.findByAmpId(contactAccount360.getAmpId());
        if (optionalContactAccount.isPresent()) {
            ContactAccount360 contactAccount = optionalContactAccount.get();
            contactAccount.setId(optionalContactAccount.get().getId());
            contactAccount.setData(contactAccount360.getData());
            contactAccount.setAmpId(contactAccount360.getAmpId());
            contactAccount.setAcct_sysco_cust_id(contactAccount360.getAcct_sysco_cust_id());
            contactAccount.setAccount_sf_id(contactAccount360.getAccount_sf_id());
            contactAccount.setContact_sf_id(contactAccount360.getContact_sf_id());
            return new ResponseEntity<>(contactAccount360Repository.save(contactAccount), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public AccountDto getContactAccountByAmpId(String amp_id) {
        return convertListToObject(contactAccount360Repository.findByAmperityId(amp_id));
    }

    @Override
    public AccountDto getContactAccountBySyscoCustomerId(String sys_cust_id) {
        return convertListToObject(contactAccount360Repository.findBySyscoCustomerId(sys_cust_id));
    }

    @Override
    public AccountDto getContactAccountByContactSfId(String contact_sf_id) {
        return convertListToObject(contactAccount360Repository.findByContactSfId(contact_sf_id));
    }

    @Override
    public AccountDto getContactAccountByAccountSfId(String account_sf_id) {
        return convertListToObject(contactAccount360Repository.findByAccountSfId(account_sf_id));
    }

    public AccountDto convertListToObject(List<Object> list) {
        AccountDto accountDto = new AccountDto();
        List<Object> stitched_list = new ArrayList<>();
        TreeMap<String, Object> sortedData;

        for (int i = 0; i < list.size(); i++) {
            sortedData = new TreeMap<> ();

            sortedData.putAll(new JSONObject(list.get(i).toString()).toMap());
            stitched_list.add(sortedData);

        }
        accountDto.setContacts(stitched_list);
        return accountDto;
    }

}
