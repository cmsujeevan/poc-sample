package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.CDTContactsAccountTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CDTContactsAccountTempRepository extends JpaRepository<CDTContactsAccountTemp, Long>, CDTContactsAccountTempCustomJsonbRepository{


    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE cdt_contacts_account_temp", nativeQuery = true)
    void truncateTable();
}
