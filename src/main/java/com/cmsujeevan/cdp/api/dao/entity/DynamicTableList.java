package com.cmsujeevan.cdp.api.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Builder
@Table(name = "dynamic_table_list")
public class DynamicTableList {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "status")
    private String status;

    @Column(name = "updated_time")
    private Timestamp updatedTime;


}
