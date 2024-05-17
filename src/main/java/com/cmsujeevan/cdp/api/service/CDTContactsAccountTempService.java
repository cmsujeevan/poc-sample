package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.dao.entity.CDTContactsAccountTemp;

import java.util.List;

public interface CDTContactsAccountTempService {

    List<CDTContactsAccountTemp> createBatchCDTContactsAccount(List<CDTContactsAccountTemp> cdtContactsAccountTemps);

    void truncateTable();
}
