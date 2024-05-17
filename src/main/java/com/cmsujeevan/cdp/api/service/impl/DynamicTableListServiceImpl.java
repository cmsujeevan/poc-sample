package com.cmsujeevan.cdp.api.service.impl;


import com.cmsujeevan.cdp.api.dao.entity.DynamicTableList;
import com.cmsujeevan.cdp.api.dao.repository.DynamicTableListRepository;
import com.cmsujeevan.cdp.api.service.DynamicTableListService;
import com.cmsujeevan.cdp.common.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicTableListServiceImpl implements DynamicTableListService {

    private final DynamicTableListRepository dynamicTableListRepository;

    public String isTableExist(String tableName) {
        Optional<DynamicTableList> dynamicTableList = dynamicTableListRepository.findByTableName(tableName);
        if(dynamicTableList.isPresent())
            return dynamicTableList.get().getTableName();
        else
            return "";
    }

    public DynamicTableList saveDynamicTable(DynamicTableList dynamicTableList) {
        return dynamicTableListRepository.save(dynamicTableList);
    }

    @Override
    public DynamicTableList getByTableName(String tableName) {
        Optional<DynamicTableList> dynamicTableList = dynamicTableListRepository.findByTableName(tableName);
        if(dynamicTableList.isPresent())
            return dynamicTableList.get();
        else
            return null;
    }

    @Override
    public void updateTableDate(String dataType) {
        DynamicTableList dynamicTableList = getByTableName(dataType);
        if(dynamicTableList != null) {
            dynamicTableList.setUpdatedTime(TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE));
        }
        dynamicTableListRepository.save(dynamicTableList);
    }


}
