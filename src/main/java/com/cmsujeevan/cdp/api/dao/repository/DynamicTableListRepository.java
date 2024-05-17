package com.cmsujeevan.cdp.api.dao.repository;



import com.cmsujeevan.cdp.api.dao.entity.DynamicTableList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface DynamicTableListRepository extends JpaRepository<DynamicTableList, Long> {

    Optional<DynamicTableList> findByTableName(String tableName);

}
