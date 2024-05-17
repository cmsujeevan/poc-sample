package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.service.Contact360TempService;
import com.cmsujeevan.cdp.api.dao.entity.Contact360Temp;
import com.cmsujeevan.cdp.api.dao.repository.Contact360TempRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class Contact360TempServiceImpl implements Contact360TempService {

    private final Contact360TempRepository contact360TempRepository;


    @Override
    public List<Contact360Temp> createBatchContact360(List<Contact360Temp> contact360List) {
        return contact360TempRepository.saveBulkList(contact360List);
    }

    @Override
    public void truncateTable() {
        contact360TempRepository.truncateTable();
    }


}
