package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360Temp;
import java.util.List;

public interface ContactAccount360TempService {

    List<ContactAccount360Temp> createBatchContactAccount360Temp(List<ContactAccount360Temp> ContactAccount360TempList);

    void truncateTable();


}
