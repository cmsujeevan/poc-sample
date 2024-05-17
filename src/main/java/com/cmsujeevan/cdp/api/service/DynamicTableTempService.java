package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.dao.entity.DynamicTableTemp;

import java.util.List;

public interface DynamicTableTempService {


    List<DynamicTableTemp> createBatchData(List<DynamicTableTemp> dynamicTableTempList);

    void truncateTable();
}
