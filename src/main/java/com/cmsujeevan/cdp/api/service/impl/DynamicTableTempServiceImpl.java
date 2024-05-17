package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.dao.entity.DynamicTableTemp;
import com.cmsujeevan.cdp.api.service.DynamicTableTempService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DynamicTableTempServiceImpl implements DynamicTableTempService {
    @PersistenceContext
    private EntityManager entityManager;

    @Qualifier("datasource")
    private final DataSource dataSource;


    @Override
    public List<DynamicTableTemp> createBatchData(List<DynamicTableTemp> dynamicTableTempList) {
            return saveBulkList(dynamicTableTempList);
    }


    public List<DynamicTableTemp> saveBulkList(List<DynamicTableTemp> entities) {
        try (Connection connection = dataSource.getConnection()) {

            PGConnection pgcon = connection.unwrap(PGConnection.class);
            CopyManager copyManager = pgcon.getCopyAPI();

            StringBuilder csvData = new StringBuilder();
            // Generate CSV data from the list of entities
            for (DynamicTableTemp entity : entities) {
                // Append the entity data to the CSV string
                csvData.append("|"+entity.getAmpId()+"|").append("\t")
                        .append("|"+entity.getSyscoCustomerId()+"|").append("\n");
            }
            // Perform the COPY operation
            copyManager.copyIn("COPY dynamic_table_temp (amperity_id,sys_cust_id) " +
                    "FROM STDIN WITH CSV DELIMITER E'\\t' QUOTE '|' ESCAPE '\\'", new StringReader(csvData.toString()));

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return entities;
    }


    @Override
    public void truncateTable() {
        String truncateTableSQL = "TRUNCATE TABLE dynamic_table_temp";
        try {
            entityManager.createNativeQuery(truncateTableSQL).executeUpdate();
        } catch (Exception e) {
            // Handle exceptions.
            e.printStackTrace();
        }
    }


}
