package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.model.dto.Contact360Dto;

public interface Contact360Service {

    void insertRecordsFromTempTable();

    Contact360Dto getContactsByAmpId(String amp_id);

    Contact360Dto getContactsByEmailId(String email_id);

    Contact360Dto getContactsByOktaUserId(String okta_user_id);

    Contact360Dto getContactsByContactSfId(String contact_sf_id);
}
