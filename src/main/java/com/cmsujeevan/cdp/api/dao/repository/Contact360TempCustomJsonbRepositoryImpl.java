package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.Contact360Temp;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Repository
public class Contact360TempCustomJsonbRepositoryImpl implements Contact360TempCustomJsonbRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Qualifier("datasource")
    private final DataSource dataSource;

    public Contact360TempCustomJsonbRepositoryImpl(EntityManager entityManager, DataSource dataSource) {
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }


    public List<Contact360Temp> saveBulkList(List<Contact360Temp> entities) {
        try (Connection connection = dataSource.getConnection()) {

            PGConnection pgcon = connection.unwrap(PGConnection.class);
            CopyManager copyManager = pgcon.getCopyAPI();

            StringBuilder csvData = new StringBuilder();
            // Generate CSV data from the list of entities
            for (Contact360Temp entity : entities) {
                // Append the entity data to the CSV string
                csvData.append("|"+entity.getData()+"|").append("\t")
                        .append("|"+entity.getAmpId()+"|").append("\t")
                        .append("|"+entity.getEmail()+"|").append("\t")
                        .append("|"+entity.getContactSfId()+"|").append("\t")
                        .append("|"+entity.getOkta_user_id()+"|").append("\n");
            }
            // Perform the COPY operation
            copyManager.copyIn("COPY contact_360_temp (data,amperity_id,email,contact_sf_id,okta_user_id) " +
                    "FROM STDIN WITH CSV DELIMITER E'\\t' QUOTE '|' ESCAPE '\\'", new StringReader(csvData.toString()));

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return entities;
    }


}

