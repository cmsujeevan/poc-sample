package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.service.ContactAccount360TempService;
import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360Temp;
import com.cmsujeevan.cdp.api.dao.repository.ContactAccount360TempRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ContactAccount360TempServiceImpl implements ContactAccount360TempService {

    private final ContactAccount360TempRepository contactAccount360TempRepository;


    @Override
    public List<ContactAccount360Temp> createBatchContactAccount360Temp(List<ContactAccount360Temp> ContactAccount360TempList) {
        return contactAccount360TempRepository.saveBulkList(ContactAccount360TempList);
    }

    @Override
    public void truncateTable() {
        contactAccount360TempRepository.truncateTable();
    }


}
