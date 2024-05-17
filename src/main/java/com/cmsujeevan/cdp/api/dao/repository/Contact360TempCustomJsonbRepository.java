package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.Contact360Temp;
import java.util.List;


public interface Contact360TempCustomJsonbRepository {


    List<Contact360Temp> saveBulkList(List<Contact360Temp> entities);

}
