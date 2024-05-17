package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ContactAccount360TempRepository extends JpaRepository<ContactAccount360Temp, Long>, ContactAccount360TempCustomJsonbRepository {


    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE contact_account_360_temp", nativeQuery = true)
    void truncateTable();
}
