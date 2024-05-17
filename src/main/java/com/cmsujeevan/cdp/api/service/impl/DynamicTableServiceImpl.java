package com.cmsujeevan.cdp.api.service.impl;



import com.cmsujeevan.cdp.api.service.DynamicTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DynamicTableServiceImpl implements DynamicTableService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createDynamicTable(String table_name) {
        String createTableSQL = "CREATE TABLE " + table_name + " ("
                + "id SERIAL PRIMARY KEY,"
                + "amperity_id VARCHAR(50),"
                + "sys_cust_id VARCHAR(25)"
                + ")";
        String createTableIndex ="CREATE INDEX idx_"+table_name+"_amp_id ON "+table_name+"(amperity_id)";
        try {
            entityManager.createNativeQuery(createTableSQL).executeUpdate();
            entityManager.createNativeQuery(createTableIndex).executeUpdate();
        } catch (Exception e) {
            // Handle exceptions, e.g., table already exists
            e.printStackTrace();
        }
    }

    @Override
    public void insertRecordsFromTempTable(String dataType) {
        String INSERT_INTO_DYNAMIC_TABLE = "INSERT INTO "+dataType+" (amperity_id,sys_cust_id)\n" +
                "SELECT amperity_id,sys_cust_id\n" +
                "FROM dynamic_table_temp t\n" +
                "WHERE NOT EXISTS (SELECT FROM "+dataType+" d " +
                "WHERE d.amperity_id = t.amperity_id AND d.sys_cust_id = t.sys_cust_id)";

        try {
            entityManager.createNativeQuery(INSERT_INTO_DYNAMIC_TABLE).executeUpdate();
        } catch (Exception e) {
            // Handle exceptions, e.g., table already exists
            e.printStackTrace();
        }
    }

}
