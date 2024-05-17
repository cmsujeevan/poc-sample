package com.cmsujeevan.cdp.api.dao.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@ToString
@Table(name = "contact_account_360_temp")
public class ContactAccount360Temp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "data")
    private JsonNode data;

    @Column(name = "amperity_id")
    private String ampId;

    @Column(name = "account_sysco_customer_id")
    private String acct_sysco_cust_id;

    @Column(name = "contact_salesforce_id")
    private String contact_sf_id;

    @Column(name = "account_salesforce_id")
    private String account_sf_id;
}
