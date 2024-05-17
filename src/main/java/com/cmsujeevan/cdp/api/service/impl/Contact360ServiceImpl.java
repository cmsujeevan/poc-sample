package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.model.dto.Contact360Dto;
import com.cmsujeevan.cdp.api.dao.repository.Contact360Repository;
import com.cmsujeevan.cdp.api.service.Contact360Service;
import com.cmsujeevan.cdp.common.constants.Constants;
import com.cmsujeevan.cdp.exception.exceptions.RecordsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class Contact360ServiceImpl implements Contact360Service {

    private final Contact360Repository contact360Repository;

    @Override
    public void insertRecordsFromTempTable() {
        contact360Repository.insertRecordsFromTempTable();
    }

    @Override
    public Contact360Dto getContactsByAmpId(String amp_id) {
        return convertListToObject(contact360Repository.findByAmpId(amp_id));
    }

    @Override
    public Contact360Dto getContactsByEmailId(String email_id) {
        return convertListToObject(contact360Repository.findByEmailId(email_id));
    }

    @Override
    public Contact360Dto getContactsByOktaUserId(String okta_user_id) {
        return convertListToObject(contact360Repository.findByOktaUserId(okta_user_id));
    }

    @Override
    public Contact360Dto getContactsByContactSfId(String contact_sf_id) {
        return convertListToObject(contact360Repository.findByContactSfId(contact_sf_id));
    }

    public Contact360Dto convertListToObject(List<Object[]> list) {
        Contact360Dto contact360Dto = new Contact360Dto();
        List<Map> final_data = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            processEntry(list.get(i)[0], list.get(i)[1], final_data);
        }
        contact360Dto.setContacts(final_data);
        return contact360Dto;
    }

    private void processEntry(Object obj1, Object obj2, List<Map> final_data) {
        List<Object> stitched_list = new ArrayList<>();
        TreeMap<String, Object> sortedData = new TreeMap<>();
        JSONObject jsonObject;
        if (obj1 != null) {
            jsonObject = new JSONObject(obj1.toString());
            sortedData.putAll(jsonObject.toMap());
        }
        if (obj2 != null) {
            stitched_list.add(new JSONObject(obj2.toString()).toMap());
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.putAll(sortedData);
        data.put(Constants.STITCHED_CONTACT, stitched_list);
        final_data.add(data);
    }
}
