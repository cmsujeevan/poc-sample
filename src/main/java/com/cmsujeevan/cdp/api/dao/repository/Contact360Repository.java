package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.Contact360;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface Contact360Repository extends JpaRepository<Contact360, Long> {


    String INSERT_INTO_CONTACT_360 = "INSERT INTO contact_360 (data,amperity_id,email,contact_sf_id,okta_user_id)\n" +
            "SELECT data,amperity_id,email,contact_sf_id,okta_user_id\n" +
            "FROM contact_360_temp t\n" +
            "WHERE  NOT EXISTS (SELECT FROM contact_360 c WHERE c.amperity_id = t.amperity_id)";


    @Modifying
    @Query(value=INSERT_INTO_CONTACT_360, nativeQuery = true)
    void insertRecordsFromTempTable();


    @Modifying
    @Query(value = "TRUNCATE TABLE contact_360", nativeQuery = true)
    void truncateTable();

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data from contact_360 c left join contact_account_360 ca on c.amperity_id=ca.amperity_id where c.amperity_id=:amp_id", nativeQuery = true)
    List<Object[]> findByAmpId(@Param("amp_id") String amp_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data from contact_360 c left join contact_account_360 ca on c.amperity_id=ca.amperity_id where c.email=:email_id", nativeQuery = true)
    List<Object[]> findByEmailId(@Param("email_id") String email_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data from contact_360 c left join contact_account_360 ca on c.amperity_id=ca.amperity_id where c.okta_user_id=:okta_user_id", nativeQuery = true)
    List<Object[]> findByOktaUserId(@Param("okta_user_id") String okta_user_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data from contact_360 c left join contact_account_360 ca on c.amperity_id=ca.amperity_id where c.contact_sf_id=:contact_sf_id", nativeQuery = true)
    List<Object[]> findByContactSfId(String contact_sf_id);
}
