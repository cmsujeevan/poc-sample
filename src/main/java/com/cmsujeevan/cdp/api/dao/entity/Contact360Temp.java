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
@Table(name = "contact_360_temp")
public class Contact360Temp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "data")
    private JsonNode data;

    @Column(name = "amperity_id")
    private String ampId;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_sf_id")
    private String contactSfId;

    @Column(name = "okta_user_id")
    private String okta_user_id;
}
