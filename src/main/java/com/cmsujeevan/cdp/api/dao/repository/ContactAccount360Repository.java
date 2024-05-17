package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactAccount360Repository extends JpaRepository<ContactAccount360, Long> {

    String INSERT_INTO_CONTACT_Account_360 =
            "INSERT INTO contact_account_360 (data,amperity_id,account_sysco_customer_id,contact_salesforce_id,account_salesforce_id)\n" +
            "SELECT data,amperity_id,account_sysco_customer_id,contact_salesforce_id,account_salesforce_id\n" +
            "FROM contact_account_360_temp t\n" +
            "WHERE  NOT EXISTS (SELECT FROM contact_account_360 ac WHERE ac.amperity_id = t.amperity_id AND  ac.account_sysco_customer_id = t.account_sysco_customer_id)";


    @Transactional
    @Modifying
    @Query(value=INSERT_INTO_CONTACT_Account_360, nativeQuery = true)
    void insertRecordsFromTempTable();

    @Query(value = "SELECT Cast(data as varchar) from contact_account_360 where amperity_id=:amp_id", nativeQuery = true)
    List<Object> findByAmperityId(@Param("amp_id") String amp_id);

    @Query(value = "SELECT Cast(data as varchar) from contact_account_360 where account_sysco_customer_id=:sys_cust_id", nativeQuery = true)
    List<Object> findBySyscoCustomerId(String sys_cust_id);

    @Query(value = "SELECT Cast(data as varchar) from contact_account_360 where contact_salesforce_id=:contact_sf_id", nativeQuery = true)
    List<Object> findByContactSfId(String contact_sf_id);

    @Query(value = "SELECT Cast(data as varchar) from contact_account_360 where account_salesforce_id=:account_sf_id", nativeQuery = true)
    List<Object> findByAccountSfId(String account_sf_id);

    Optional<ContactAccount360> findByAmpId(String amp_id);
}
