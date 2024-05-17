package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.service.CDTContactsAccountTempService;
import com.cmsujeevan.cdp.api.dao.entity.CDTContactsAccountTemp;
import com.cmsujeevan.cdp.api.dao.repository.CDTContactsAccountTempRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CDTContactsAccountTempServiceImpl implements CDTContactsAccountTempService {

    private final CDTContactsAccountTempRepository cdtContactsAccountTempRepository;


    @Override
    public List<CDTContactsAccountTemp> createBatchCDTContactsAccount(List<CDTContactsAccountTemp> cdtContactsAccountTemps) {
        return cdtContactsAccountTempRepository.saveBulkList(cdtContactsAccountTemps);
    }

    @Override
    public void truncateTable() {
        cdtContactsAccountTempRepository.truncateTable();
    }


}
