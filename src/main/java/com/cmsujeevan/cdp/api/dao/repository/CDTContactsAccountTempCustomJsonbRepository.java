package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.CDTContactsAccountTemp;

import java.util.List;


public interface CDTContactsAccountTempCustomJsonbRepository {


    List<CDTContactsAccountTemp> saveBulkList(List<CDTContactsAccountTemp> entities);

}
