package com.cmsujeevan.cdp.api.dao.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
@ToString
public class DynamicTable {


    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amperity_id")
    private String ampId;

    @Column(name = "sys_cust_id")
    private String syscoCustomerId;

//    @Column(name = "file_name")
//    private String fileName;


}
