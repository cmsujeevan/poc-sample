package com.cmsujeevan.cdp.api.service;


import com.cmsujeevan.cdp.api.dao.entity.DynamicTableList;

public interface DynamicTableListService {

    String isTableExist(String tableName);
    DynamicTableList saveDynamicTable(DynamicTableList dynamicTableList);
    DynamicTableList getByTableName(String tableName);
    void updateTableDate(String dataType);
}
