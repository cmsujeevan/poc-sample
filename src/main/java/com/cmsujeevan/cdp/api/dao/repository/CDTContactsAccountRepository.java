package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.CDTContactsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CDTContactsAccountRepository extends JpaRepository<CDTContactsAccount, Long> {

    String INSERT_INTO_CDT_CONTACTS_ACCOUNT = "INSERT INTO cdt_contacts_account (data,amperity_id,email,contact_id,okta_user_id,contact_account_pk)\n" +
            "SELECT data,amperity_id,email,contact_id,okta_user_id,contact_account_pk\n" +
            "FROM cdt_contacts_account_temp t\n" +
            "WHERE  NOT EXISTS (SELECT FROM cdt_contacts_account c WHERE c.contact_account_pk = t.contact_account_pk)";


    @Transactional
    @Modifying
    @Query(value=INSERT_INTO_CDT_CONTACTS_ACCOUNT, nativeQuery = true)
    void insertRecordsFromTempTable();

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data, " +
            "(SELECT STRING_AGG(cta.account_sysco_customer_id, ', ') FROM contact_account_360 cta WHERE cta.contact_salesforce_id=c.contact_sf_id and cta.data ->> 'account_status' = 'Active') AS activate_accounts "+
            "from contact_360 c inner join cdt_contacts_account ca on c.amperity_id=ca.amperity_id where c.amperity_id=:amp_id", nativeQuery = true)
    List<Object[]> findByAmpId(@Param("amp_id") String amp_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data, " +
            "(SELECT STRING_AGG(cta.account_sysco_customer_id, ', ') FROM contact_account_360 cta WHERE cta.contact_salesforce_id=c.contact_sf_id and cta.data ->> 'account_status' = 'Active') AS activate_accounts "+
            "from contact_360 c inner join cdt_contacts_account ca on ca.amperity_id=c.amperity_id " +
            "WHERE ca.amperity_id IN (SELECT amperity_id FROM cdt_contacts_account WHERE email=:email_id)", nativeQuery = true)
    List<Object[]> findByEmailId(@Param("email_id") String email_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data, " +
            "(SELECT STRING_AGG(cta.account_sysco_customer_id, ', ') FROM contact_account_360 cta WHERE cta.contact_salesforce_id=c.contact_sf_id and cta.data ->> 'account_status' = 'Active') AS activate_accounts "+
            "from contact_360 c inner join cdt_contacts_account ca on ca.amperity_id=c.amperity_id " +
            "WHERE ca.amperity_id IN (SELECT amperity_id FROM contact_360 WHERE okta_user_id=:okta_user_id)", nativeQuery = true)
    List<Object[]> findByOktaUserId(@Param("okta_user_id") String okta_user_id);

    @Query(value = "SELECT Cast(c.data as varchar) c_data, Cast(ca.data as varchar) ca_data, " +
            "(SELECT STRING_AGG(cta.account_sysco_customer_id, ', ') FROM contact_account_360 cta WHERE cta.contact_salesforce_id=c.contact_sf_id and cta.data ->> 'account_status' = 'Active') AS activate_accounts "+
            "from contact_360 c inner join cdt_contacts_account ca on ca.amperity_id=c.amperity_id " +
            "WHERE ca.amperity_id IN (SELECT amperity_id FROM cdt_contacts_account WHERE contact_id=:contact_id)", nativeQuery = true)
    List<Object[]> findByContactSfId(String contact_id);
}
