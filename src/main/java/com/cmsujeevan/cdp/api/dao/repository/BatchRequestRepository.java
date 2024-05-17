package com.cmsujeevan.cdp.api.dao.repository;

import com.cmsujeevan.cdp.api.dao.entity.BatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRequestRepository extends JpaRepository<BatchRequest, String> {
}
