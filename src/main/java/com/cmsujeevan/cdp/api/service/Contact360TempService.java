package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.dao.entity.Contact360Temp;

import java.util.List;

public interface Contact360TempService {

    List<Contact360Temp> createBatchContact360(List<Contact360Temp> contact360TempList);

    void truncateTable();
}
