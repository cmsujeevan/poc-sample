package com.cmsujeevan.cdp.api.dao.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "dynamic_table_temp")
public class DynamicTableTemp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amperity_id")
    private String ampId;

    @Column(name = "sys_cust_id")
    private String syscoCustomerId;


}
