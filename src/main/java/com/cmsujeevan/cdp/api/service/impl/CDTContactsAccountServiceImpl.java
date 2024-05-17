package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.model.dto.Contact360Dto;
import com.cmsujeevan.cdp.api.service.CDTContactsAccountService;
import com.cmsujeevan.cdp.api.dao.repository.CDTContactsAccountRepository;
import com.cmsujeevan.cdp.common.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class CDTContactsAccountServiceImpl implements CDTContactsAccountService {

    private final CDTContactsAccountRepository cdtContactsAccountRepository;

    @Override
    public void insertRecordsFromTempTable() {
        cdtContactsAccountRepository.insertRecordsFromTempTable();
    }

    @Override
    public Contact360Dto getContactsByAmpId(String amp_id) {
        return convertListToObject(cdtContactsAccountRepository.findByAmpId(amp_id));
    }

    @Override
    public Contact360Dto getContactsByEmailId(String email_id) {
        return convertListToObject(cdtContactsAccountRepository.findByEmailId(email_id));
    }

    @Override
    public Contact360Dto getContactsByOktaUserId(String okta_user_id) {
        return convertListToObject(cdtContactsAccountRepository.findByOktaUserId(okta_user_id));
    }

    @Override
    public Contact360Dto getContactsByContactSfId(String contact_sf_id) {
        return convertListToObject(cdtContactsAccountRepository.findByContactSfId(contact_sf_id));
    }

    public Contact360Dto convertListToObject(List<Object[]> list) {
        Contact360Dto contact360Dto = new Contact360Dto();
        List<Map> final_data = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            processEntry(list.get(i)[0], list.get(i)[1], list.get(i)[2], final_data);
        }
        contact360Dto.setContacts(final_data);
        return contact360Dto;
    }

    private void processEntry(Object goldContact, Object stitchedContact, Object activeAccounts, List<Map> final_data) {
        JSONObject jsonObject;
        List<Object> stitched_list = new ArrayList<>();
        List<Object> active_acct_list = new ArrayList<>();
        TreeMap<String, Object> sortedData = new TreeMap<>();
        if (goldContact != null) {
            jsonObject = new JSONObject(goldContact.toString());
            sortedData.putAll(jsonObject.toMap());
        }
        if (stitchedContact != null) {
            stitched_list.add(new JSONObject(stitchedContact.toString()).toMap());
        }
        if (activeAccounts != null) {
            active_acct_list.addAll(Arrays.asList(activeAccounts.toString().split("\\s*,\\s*")));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.putAll(sortedData);
        data.put(Constants.ACTIVE_ACCOUNTS, active_acct_list);
        data.put(Constants.STITCHED_CONTACT, stitched_list);
        final_data.add(data);
    }
}
