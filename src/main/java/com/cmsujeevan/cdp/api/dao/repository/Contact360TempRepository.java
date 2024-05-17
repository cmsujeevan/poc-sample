package com.cmsujeevan.cdp.api.dao.repository;


import com.cmsujeevan.cdp.api.dao.entity.Contact360Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface Contact360TempRepository extends JpaRepository<Contact360Temp, Long>, Contact360TempCustomJsonbRepository {


    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE contact_360_temp", nativeQuery = true)
    void truncateTable();
}
