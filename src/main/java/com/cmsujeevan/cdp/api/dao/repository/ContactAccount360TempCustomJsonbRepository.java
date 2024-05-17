package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360Temp;
import java.util.List;


public interface ContactAccount360TempCustomJsonbRepository {
    

    List<ContactAccount360Temp> saveBulkList(List<ContactAccount360Temp> entities);

}
