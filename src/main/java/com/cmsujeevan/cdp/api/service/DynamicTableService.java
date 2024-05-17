package com.cmsujeevan.cdp.api.service;



public interface DynamicTableService {

    void createDynamicTable(String table_name);

    void insertRecordsFromTempTable(String dataType);
}
